const api = '/api/transcriptions';
const language = document.querySelector('#language');
const liveText = document.querySelector('#liveText');
const status = document.querySelector('#status');
const record = document.querySelector('#record');
const support = document.querySelector('#support');
const historyList = document.querySelector('#historyList');
const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
let recognition;
let listening = false;

if (!SpeechRecognition) {
    support.textContent = 'Live recognition is not supported by this browser. Try Chrome or Edge.';
    record.disabled = true;
} else {
    recognition = new SpeechRecognition();
    recognition.continuous = true;
    recognition.interimResults = true;
    recognition.onresult = event => {
        let finalText = '';
        let interimText = '';
        for (let i = event.resultIndex; i < event.results.length; i++) {
            const text = event.results[i][0].transcript;
            if (event.results[i].isFinal) finalText += text + ' ';
            else interimText += text;
        }
        if (finalText) liveText.value += finalText;
        status.textContent = interimText ? `Hearing: ${interimText}` : 'Listening...';
    };
    recognition.onerror = event => {
        status.textContent = `Recognition error: ${event.error}`;
        listening = false;
        record.textContent = '● Start listening';
    };
    recognition.onend = () => {
        if (listening) recognition.start();
    };
}

record.onclick = () => {
    if (!recognition) return;
    recognition.lang = language.value;
    listening = !listening;
    if (listening) { recognition.start(); record.textContent = '■ Stop listening'; status.textContent = 'Listening...'; }
    else { recognition.stop(); record.textContent = '● Start listening'; status.textContent = 'Ready to listen'; }
};
document.querySelector('#clear').onclick = () => { liveText.value = ''; status.textContent = 'Ready to listen'; };
document.querySelector('#refresh').onclick = loadHistory;
document.querySelector('#save').onclick = async () => {
    const content = liveText.value.trim();
    if (!content) return alert('Speak or type a transcript before saving.');
    const title = prompt('Name this transcript:', `Session ${new Date().toLocaleString()}`);
    if (!title) return;
    await request(api, 'POST', { title, content, language: language.value });
    liveText.value = '';
    await loadHistory();
};

async function loadHistory() {
    const entries = await request(api);
    historyList.innerHTML = entries.length ? entries.map(item => `
        <article class="history-item">
            <div class="item-actions"><button onclick="editEntry('${item.id}')">Edit</button><button onclick="deleteEntry('${item.id}')">Delete</button></div>
            <h3>${escapeHtml(item.title)}</h3><p>${escapeHtml(item.content)}</p>
            <span class="meta">${escapeHtml(item.language)} · ${new Date(item.updatedAt).toLocaleString()}</span>
        </article>`).join('') : '<p class="empty">No saved transcripts yet.</p>';
}
async function editEntry(id) {
    const item = await request(`${api}/${id}`);
    const title = prompt('Title:', item.title); if (!title) return;
    const content = prompt('Transcript:', item.content); if (!content) return;
    await request(`${api}/${id}`, 'PUT', { title, content, language: item.language }); loadHistory();
}
async function deleteEntry(id) {
    if (!confirm('Delete this transcript?')) return;
    await request(`${api}/${id}`, 'DELETE'); loadHistory();
}
async function request(url, method = 'GET', body) {
    const response = await fetch(url, { method, headers: body ? {'Content-Type': 'application/json'} : {}, body: body && JSON.stringify(body) });
    if (!response.ok) throw new Error((await response.json()).message || 'Request failed');
    return response.status === 204 ? null : response.json();
}
function escapeHtml(value) { return value.replace(/[&<>"']/g, char => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[char])); }
loadHistory().catch(error => { historyList.innerHTML = `<p class="empty">${escapeHtml(error.message)}</p>`; });
