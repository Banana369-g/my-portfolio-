// Portfolio script: project cards, the project viewer (preview + code),
// and edit mode (index.html?edit) for uploading a photo and projects.
// Uploads are kept in this browser's IndexedDB; projects in the "projects"
// folder come from projects-data.js (made by build-projects.ps1).
(function () {
  'use strict';

  const $ = (sel) => document.querySelector(sel);
  const editing = new URLSearchParams(location.search).has('edit');
  const builtIn = (window.PROJECTS || []).map((p) => Object.assign({ source: 'folder' }, p));
  let uploaded = [];
  let all = [];

  // ---------- small helpers ----------
  const esc = (s) => String(s).replace(/[&<>"']/g, (c) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' })[c]);
  const both = (en, ja) => `<span lang="en">${en}</span><span lang="ja">${ja}</span>`;
  const isHtml = (path) => /\.html?$/i.test(path);
  const ext = (path) => (path.match(/\.([^./]+)$/) || ['', ''])[1].toLowerCase();
  const findFile = (p, path) => p.files.find((f) => f.path.toLowerCase() === path.toLowerCase());
  const folderUrl = (p, path) => 'projects/' + [p.folder].concat(path.split('/')).map(encodeURIComponent).join('/');

  // ---------- storage (IndexedDB) ----------
  const db = new Promise((resolve, reject) => {
    const req = indexedDB.open('portfolio', 1);
    req.onupgradeneeded = () => {
      req.result.createObjectStore('projects', { keyPath: 'id' });
      req.result.createObjectStore('settings');
    };
    req.onsuccess = () => resolve(req.result);
    req.onerror = () => reject(req.error);
  });
  function store(name, mode, action) {
    return db.then((d) => new Promise((resolve, reject) => {
      const tx = d.transaction(name, mode);
      const req = action(tx.objectStore(name));
      tx.oncomplete = () => resolve(req.result);
      tx.onerror = () => reject(tx.error);
    }));
  }

  // ---------- social links (from links.js) ----------
  const links = window.LINKS || {};
  document.querySelectorAll('a[data-link]').forEach((a) => {
    let url = (links[a.dataset.link] || '').trim();
    if (!url) return;
    if (a.dataset.link === 'github' && !url.includes('/')) url = 'https://github.com/' + url.replace(/^@/, '');
    if (!/^https?:\/\//i.test(url)) url = 'https://' + url;
    a.href = url;
  });

  // ---------- on scroll: back to top button, header background, progress bar ----------
  const toTop = $('#to-top');
  const header = $('.top');
  const progress = $('#progress');
  function onScroll() {
    const y = window.scrollY;
    toTop.classList.toggle('show', y > 600);
    header.classList.toggle('scrolled', y > 20);
    const max = document.documentElement.scrollHeight - window.innerHeight;
    progress.style.setProperty('--p', max > 0 ? Math.min(1, y / max) : 0);
  }
  window.addEventListener('scroll', onScroll, { passive: true });
  onScroll();

  // ---------- fade sections in as they come into view ----------
  // Items next to each other appear one after another (--d is the delay).
  const reveals = document.querySelectorAll('.reveal');
  if ('IntersectionObserver' in window) {
    const seen = new IntersectionObserver((entries) => {
      entries.filter((en) => en.isIntersecting).forEach((en, i) => {
        en.target.style.setProperty('--d', (i * 0.08) + 's');
        en.target.classList.add('in');
        seen.unobserve(en.target);
      });
    }, { rootMargin: '0px 0px -8% 0px' });
    reveals.forEach((el) => seen.observe(el));

    // highlight the menu link of the section on screen
    const navLinks = document.querySelectorAll('nav a');
    const spy = new IntersectionObserver((entries) => {
      entries.forEach((en) => {
        if (!en.isIntersecting) return;
        navLinks.forEach((a) => a.classList.toggle('active', a.getAttribute('href') === '#' + en.target.id));
      });
    }, { rootMargin: '-45% 0px -50% 0px' });
    document.querySelectorAll('main section[id]').forEach((s) => spy.observe(s));
  } else {
    reveals.forEach((el) => el.classList.add('in'));
  }

  // ---------- soft light on cards that follows the mouse ----------
  document.addEventListener('pointermove', (e) => {
    const c = e.target.closest && e.target.closest('.glass');
    if (!c) return;
    const r = c.getBoundingClientRect();
    c.style.setProperty('--mx', (e.clientX - r.left) + 'px');
    c.style.setProperty('--my', (e.clientY - r.top) + 'px');
  }, { passive: true });

  // ---------- profile photo ----------
  const photo = $('#photo');
  const defaultPhoto = photo.getAttribute('src');
  store('settings', 'readonly', (s) => s.get('photo')).then((url) => { if (url) photo.src = url; }).catch(() => {});

  function fileToDataUrl(file) {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = () => resolve(reader.result);
      reader.onerror = () => reject(reader.error);
      reader.readAsDataURL(file);
    });
  }

  // Crop the middle square and shrink it to 600x600 so it loads fast.
  async function squarePhoto(file) {
    const img = new Image();
    img.src = await fileToDataUrl(file);
    await img.decode();
    const side = Math.min(img.naturalWidth, img.naturalHeight);
    const canvas = document.createElement('canvas');
    canvas.width = canvas.height = 600;
    canvas.getContext('2d').drawImage(img, (img.naturalWidth - side) / 2, (img.naturalHeight - side) / 2, side, side, 0, 0, 600, 600);
    return canvas.toDataURL('image/jpeg', 0.88);
  }

  // ---------- project cards ----------
  const grid = $('#project-grid');

  function thumb(p) {
    if (p.source === 'folder') {
      return `<iframe src="${folderUrl(p, p.entry)}" scrolling="no" loading="lazy" tabindex="-1" aria-hidden="true"></iframe>`;
    }
    return `<iframe srcdoc="${esc(buildPage(p, p.entry))}" sandbox="" scrolling="no" loading="lazy" tabindex="-1" aria-hidden="true"></iframe>`;
  }

  // "java" if the tech list mentions Java, otherwise "web"
  const kindOf = (p) => ((p.tech || []).some((t) => /^java$/i.test(t)) ? 'java' : 'web');

  function card(p, i) {
    const tags = (p.tech || []).map((t) => `<li>${esc(t)}</li>`).join('');
    const kind = kindOf(p);
    const label = kind === 'java' ? '<i class="fa-brands fa-java" aria-hidden="true"></i> Java' : '<i class="fa-brands fa-html5" aria-hidden="true"></i> Web';
    return `<article class="card glass project" data-kind="${kind}"${filter !== 'all' && filter !== kind ? ' hidden' : ''}>
      <div class="thumb"><span class="kind ${kind}">${label}</span>${thumb(p)}</div>
      <div class="project-body">
        ${p.source === 'upload' ? `<span class="badge wip">${both('Uploaded · this browser only', 'アップロード · このブラウザのみ')}</span>` : ''}
        <h3>${esc(p.title)}</h3>
        ${p.description ? `<p>${esc(p.description)}</p>` : ''}
        ${tags ? `<ul class="tags">${tags}</ul>` : ''}
        <div class="btns">
          <button class="btn small solid" type="button" data-view="${i}" data-tab="preview">${both('Preview', 'プレビュー')}</button>
          <button class="btn small" type="button" data-view="${i}" data-tab="code">${both('View code', 'コードを見る')}</button>
          ${p.source === 'upload' && editing ? `<button class="btn small" type="button" data-delete="${esc(p.id)}">${both('Delete', '削除')}</button>` : ''}
        </div>
      </div>
    </article>`;
  }

  function render() {
    all = builtIn.concat(uploaded);
    let html = all.map(card).join('');
    if (editing) {
      html += `<button class="card glass add-card" type="button" data-open-upload><span><b>+</b>${both('Upload a web project', 'Web制作物をアップロード')}</span></button>`;
    }
    grid.innerHTML = html;
    updateCounts();
  }

  // ---------- project filter (All / Java / Web) ----------
  // Cards are only hidden, not rebuilt, so the preview thumbnails don't reload.
  let filter = 'all';
  const filters = $('#filters');

  function updateCounts() {
    const count = { all: all.length, java: 0, web: 0 };
    all.forEach((p) => { count[kindOf(p)]++; });
    filters.querySelectorAll('[data-filter]').forEach((b) => { b.querySelector('small').textContent = count[b.dataset.filter]; });
    $('#stat-projects').textContent = all.length;
  }

  filters.addEventListener('click', (e) => {
    const b = e.target.closest('[data-filter]');
    if (!b) return;
    filter = b.dataset.filter;
    filters.querySelectorAll('[data-filter]').forEach((x) => x.setAttribute('aria-pressed', String(x === b)));
    grid.querySelectorAll('.project').forEach((c) => { c.hidden = filter !== 'all' && c.dataset.kind !== filter; });
  });

  // Re-render only when something changed, so the preview thumbnails don't reload for nothing.
  function loadUploads() {
    const before = uploaded.length;
    return store('projects', 'readonly', (s) => s.getAll())
      .then((list) => { uploaded = (list || []).sort((a, b) => a.added - b.added); })
      .catch(() => { uploaded = []; })
      .then(() => { if (uploaded.length || before) render(); });
  }

  grid.addEventListener('click', async (e) => {
    const view = e.target.closest('[data-view]');
    if (view) openProject(all[Number(view.dataset.view)], view.dataset.tab);
    const del = e.target.closest('[data-delete]');
    if (del && confirm('Delete this uploaded project from this browser?')) {
      await store('projects', 'readwrite', (s) => s.delete(del.dataset.delete));
      loadUploads();
    }
  });

  // ---------- previews of uploaded projects ----------
  // An uploaded project only exists inside this page, so its HTML is rebuilt
  // with every local file (CSS, images, scripts) turned into a data: URL.
  // Links to its other pages send a message back here to switch pages.
  const MIME = { html: 'text/html', htm: 'text/html', css: 'text/css', js: 'text/javascript', json: 'application/json', svg: 'image/svg+xml' };
  const isLocal = (ref) => ref && !/^([a-z][a-z0-9+.-]*:|\/\/|#)/i.test(ref.trim());

  function resolvePath(from, ref) {
    const parts = from.split('/');
    parts.pop();
    for (const seg of ref.trim().split(/[?#]/)[0].split('/')) {
      if (seg === '..') parts.pop();
      else if (seg && seg !== '.') parts.push(seg);
    }
    try { return parts.map(decodeURIComponent).join('/'); } catch (err) { return parts.join('/'); }
  }

  function dataUrl(p, file) {
    if (file.kind !== 'code') return file.data;
    const text = ext(file.path) === 'css' ? rewriteCss(p, file.path, file.text) : file.text;
    return `data:${MIME[ext(file.path)] || 'text/plain'};charset=utf-8,${encodeURIComponent(text)}`;
  }

  function rewriteCss(p, from, css) {
    return css.replace(/url\(\s*(['"]?)([^'")]+)\1\s*\)/gi, (m, q, ref) => {
      const file = isLocal(ref) && findFile(p, resolvePath(from, ref));
      return file ? `url("${dataUrl(p, file)}")` : m;
    });
  }

  function buildPage(p, path) {
    const page = findFile(p, path);
    if (!page) return '<p style="font-family:sans-serif">Page not found: ' + esc(path) + '</p>';
    const html = rewriteCss(p, path, page.text).replace(/\b(src|href)(\s*=\s*)(["'])(.*?)\3/gi, (m, attr, eq, q, ref) => {
      if (!isLocal(ref)) return m;
      const file = findFile(p, resolvePath(path, ref));
      if (!file) return m;
      if (isHtml(file.path)) return `${attr}${eq}${q}#__page:${encodeURIComponent(file.path)}${q}`;
      return `${attr}${eq}${q}${dataUrl(p, file)}${q}`;
    });
    return html + '<script>document.addEventListener("click",function(e){var a=e.target.closest&&e.target.closest(\'a[href^="#__page:"]\');' +
      'if(a){e.preventDefault();parent.postMessage({portfolioPage:decodeURIComponent(a.getAttribute("href").slice(8))},"*");}});<\/script>';
  }

  // ---------- project viewer ----------
  const viewer = $('#viewer');
  const frame = $('#viewer-frame');
  const pagesEl = $('#viewer-pages');
  const filesEl = $('#viewer-files');
  const codeScroll = $('#code-scroll');
  const newTab = $('#viewer-newtab');
  let current = null;
  let currentText = '';

  function openProject(p, tab) {
    current = p;
    $('#viewer-title').textContent = p.title;
    const pages = p.files.filter((f) => isHtml(f.path));
    pagesEl.innerHTML = pages.length > 1
      ? pages.map((f) => `<button type="button" data-page="${esc(f.path)}">${esc(f.path)}</button>`).join('')
      : '';
    filesEl.innerHTML = p.files.map((f) => `<li><button type="button" data-file="${esc(f.path)}">${esc(f.path)}</button></li>`).join('');
    newTab.hidden = p.source !== 'folder';
    showPage(p.entry);
    showFile(p.entry);
    selectTab(tab);
    viewer.showModal();
  }

  const isFolder = () => current.source === 'folder';

  function showPage(path) {
    if (isFolder()) {
      frame.removeAttribute('sandbox');
      frame.removeAttribute('srcdoc');
      frame.src = folderUrl(current, path);
      newTab.href = frame.src;
    } else {
      frame.setAttribute('sandbox', 'allow-scripts allow-forms allow-popups allow-modals');
      frame.removeAttribute('src');
      frame.srcdoc = buildPage(current, path);
    }
    pagesEl.querySelectorAll('button').forEach((b) => b.setAttribute('aria-current', String(b.dataset.page === path)));
  }

  function showFile(path) {
    const file = findFile(current, path) || current.files[0];
    if (!file) return;
    filesEl.querySelectorAll('button').forEach((b) => b.setAttribute('aria-current', String(b.dataset.file === file.path)));
    $('#code-name').textContent = file.path;
    $('#code-copy').hidden = file.kind !== 'code';
    currentText = file.text || '';
    if (file.kind === 'code') {
      const lang = { html: 'xml', htm: 'xml', js: 'javascript', md: 'markdown', py: 'python', txt: 'plaintext' }[ext(file.path)] || ext(file.path);
      let body = esc(file.text);
      if (window.hljs && hljs.getLanguage(lang)) body = hljs.highlight(file.text, { language: lang, ignoreIllegals: true }).value;
      const count = file.text.split('\n').length;
      const nums = Array.from({ length: count }, (_, i) => i + 1).join('\n');
      codeScroll.innerHTML = `<pre class="code"><span class="nums" aria-hidden="true">${nums}</span><code class="hljs">${body}</code></pre>`;
    } else if (file.kind === 'image') {
      const src = isFolder() ? folderUrl(current, file.path) : file.data;
      codeScroll.innerHTML = `<img src="${esc(src)}" alt="${esc(file.path)}">`;
    } else {
      codeScroll.innerHTML = `<p class="hint" style="padding:16px">${both('This file type can’t be shown here.', 'この種類のファイルはここでは表示できません。')}</p>`;
    }
    codeScroll.scrollTop = 0;
  }

  function selectTab(name) {
    viewer.querySelectorAll('[data-tab]').forEach((b) => b.setAttribute('aria-selected', String(b.dataset.tab === name)));
    viewer.querySelectorAll('[data-pane]').forEach((pane) => { pane.hidden = pane.dataset.pane !== name; });
  }

  viewer.addEventListener('click', (e) => {
    const t = e.target.closest('button');
    if (!t) return;
    if (t.dataset.tab) selectTab(t.dataset.tab);
    if (t.dataset.page) showPage(t.dataset.page);
    if (t.dataset.file) showFile(t.dataset.file);
  });
  viewer.addEventListener('close', () => { frame.removeAttribute('srcdoc'); frame.src = 'about:blank'; });
  window.addEventListener('message', (e) => {
    if (current && e.source === frame.contentWindow && e.data && e.data.portfolioPage) showPage(e.data.portfolioPage);
  });

  $('#code-copy').addEventListener('click', async (e) => {
    const btn = e.currentTarget;
    try {
      await navigator.clipboard.writeText(currentText);
    } catch (err) {
      const area = document.createElement('textarea');
      area.value = currentText;
      document.body.appendChild(area);
      area.select();
      document.execCommand('copy');
      area.remove();
    }
    const old = btn.innerHTML;
    btn.innerHTML = both('Copied!', 'コピーしました');
    setTimeout(() => { btn.innerHTML = old; }, 1500);
  });

  // Close buttons inside any dialog
  document.querySelectorAll('dialog [data-close]').forEach((b) => b.addEventListener('click', () => b.closest('dialog').close()));

  // ---------- edit mode ----------
  if (editing) {
    $('#editbar').hidden = false;

    const photoInput = $('#photo-input');
    $('#photo-btn').addEventListener('click', () => photoInput.click());
    photoInput.addEventListener('change', async () => {
      const file = photoInput.files[0];
      if (!file) return;
      const url = await squarePhoto(file);
      await store('settings', 'readwrite', (s) => s.put(url, 'photo'));
      photo.src = url;
      photoInput.value = '';
    });
    $('#photo-reset').addEventListener('click', async () => {
      await store('settings', 'readwrite', (s) => s.delete('photo'));
      photo.src = defaultPhoto;
    });

    const upload = $('#upload');
    const form = $('#upload-form');
    const errorEl = $('#up-error');
    const CODE = /\.(html?|css|js|json|txt|md|java|sql|py)$/i;
    const IMAGE = /\.(png|jpe?g|gif|webp|svg|avif|ico|bmp)$/i;
    const MAX = 5 * 1024 * 1024;
    let pending = [];
    let skipped = 0;

    document.addEventListener('click', (e) => {
      if (!e.target.closest('[data-open-upload]')) return;
      form.reset();
      pending = [];
      $('#up-count').textContent = '';
      $('#up-entry').innerHTML = '';
      errorEl.hidden = true;
      upload.showModal();
    });

    async function readFiles(list) {
      pending = [];
      skipped = 0;
      for (const f of list) {
        // "MySite/css/style.css" -> "css/style.css"
        const path = f.webkitRelativePath ? f.webkitRelativePath.split('/').slice(1).join('/') : f.name;
        if (path.split('/').some((part) => part.startsWith('.'))) continue; // .vscode, .git, ...
        if (f.size > MAX) { skipped++; continue; }
        if (CODE.test(path)) pending.push({ path, kind: 'code', text: await f.text() });
        else if (IMAGE.test(path)) pending.push({ path, kind: 'image', data: await fileToDataUrl(f) });
        else pending.push({ path, kind: 'other', data: await fileToDataUrl(f) });
      }
      pending.sort((a, b) => a.path.localeCompare(b.path));
      const pages = pending.filter((f) => isHtml(f.path)).map((f) => f.path);
      const first = pages.find((p) => /(^|\/)index\.html?$/i.test(p)) || pages[0];
      $('#up-entry').innerHTML = pages.map((p) => `<option${p === first ? ' selected' : ''}>${esc(p)}</option>`).join('');
      $('#up-count').textContent = `${pending.length} files` + (skipped ? ` (${skipped} over 5 MB skipped)` : '');
      const title = $('#up-title');
      if (!title.value && list[0] && list[0].webkitRelativePath) title.value = list[0].webkitRelativePath.split('/')[0];
    }
    $('#up-folder').addEventListener('change', (e) => readFiles(e.target.files));
    $('#up-files').addEventListener('change', (e) => readFiles(e.target.files));

    form.addEventListener('submit', async (e) => {
      e.preventDefault();
      const entry = $('#up-entry').value;
      if (!entry) {
        errorEl.textContent = 'Choose a folder or files that include at least one .html page.';
        errorEl.hidden = false;
        return;
      }
      const project = {
        id: 'u' + Date.now(),
        source: 'upload',
        added: Date.now(),
        title: $('#up-title').value.trim(),
        description: $('#up-desc').value.trim(),
        tech: $('#up-tech').value.split(',').map((t) => t.trim()).filter(Boolean),
        entry,
        files: pending,
      };
      try {
        await store('projects', 'readwrite', (s) => s.put(project));
      } catch (err) {
        errorEl.textContent = 'Could not save: ' + err.message;
        errorEl.hidden = false;
        return;
      }
      upload.close();
      await loadUploads();
      grid.lastElementChild.previousElementSibling.scrollIntoView({ behavior: 'smooth', block: 'center' });
    });
  }

  render();
  loadUploads();
})();
