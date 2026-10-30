// Browser port of Project1.java (battle game) and Teamwork02.java (bank tracker).
// The Java programs read from Scanner and print with System.out.println;
// here print() writes a line to the console box and input() waits for Enter.
(function () {
  'use strict';

  const $ = (id) => document.getElementById(id);
  const out = $('out');
  const form = $('line');
  const field = $('in');
  const promptEl = $('prompt');
  const chipsEl = $('chips');

  // Don't grab focus until the visitor touches this page
  // (it also runs as a small preview inside the portfolio).
  let touched = false;
  document.addEventListener('pointerdown', () => { touched = true; });
  document.addEventListener('keydown', () => { touched = true; });

  // ---------- console ----------
  let waiting = null;
  let run = 0;

  function print(text, cls) {
    const line = document.createElement('div');
    line.textContent = text === undefined ? '' : String(text);
    if (cls) line.className = cls;
    out.appendChild(line);
    out.scrollTop = out.scrollHeight;
  }
  const sound = (file) => print('♪ ' + file, 'sound');

  // Like scan.nextLine(): shows the prompt and resolves with what was typed.
  function input(prompt, choices) {
    promptEl.textContent = prompt;
    chipsEl.innerHTML = (choices || []).map((c) =>
      `<button type="button" data-v="${c[0]}">${c[0]}${c[1] ? ' · ' + c[1] : ''}</button>`).join('');
    if (touched) field.focus({ preventScroll: true });
    const id = run;
    return new Promise((resolve) => { waiting = { id, prompt, resolve }; });
  }

  function submit(value) {
    if (!waiting || waiting.id !== run) return;
    const w = waiting;
    waiting = null;
    const line = document.createElement('div');
    line.className = 'echo';
    line.append(w.prompt);
    const b = document.createElement('b');
    b.textContent = value;
    line.append(b);
    out.appendChild(line);
    out.scrollTop = out.scrollHeight;
    promptEl.textContent = '';
    chipsEl.innerHTML = '';
    w.resolve(value);
  }
  form.addEventListener('submit', (e) => { e.preventDefault(); submit(field.value); field.value = ''; });
  chipsEl.addEventListener('click', (e) => { const c = e.target.closest('button'); if (c) submit(c.dataset.v); });

  // scan.nextInt(): keeps asking until it gets a whole number
  async function readInt(prompt, choices) {
    for (;;) {
      const v = (await input(prompt, choices)).trim();
      if (/^[+-]?\d+$/.test(v)) return parseInt(v, 10);
      print('Please enter a number.', 'bad');
    }
  }

  // Java prints doubles as 500.0, 12.5 or 1.9E12
  function jd(n) {
    const a = Math.abs(n);
    if (a === 0) return '0.0';
    if (a >= 1e-3 && a < 1e7) { const s = String(n); return s.includes('.') ? s : s + '.0'; }
    let [m, e] = n.toExponential().split('e');
    if (!m.includes('.')) m += '.0';
    return m + 'E' + Number(e);
  }
  const rand = (n) => Math.floor(Math.random() * n); // r.nextInt(n)
  const pad = (n) => String(n).padStart(2, '0');
  const today = () => { const d = new Date(); return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`; };
  const now = () => { const d = new Date(); return `${today()} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`; };

  // ---------- Project1.java: battle game ----------
  const hpBox = $('hp');
  function showHp(p, hp, current, hit) {
    hpBox.hidden = false;
    for (let i = 0; i < 2; i++) {
      $('n' + i).textContent = p[i];
      $('h' + i).textContent = Math.max(0, hp[i]) + ' HP';
      const bar = $('b' + i);
      bar.style.width = Math.max(0, hp[i]) + '%';
      bar.classList.toggle('low', hp[i] <= 30);
      const f = bar.closest('.fighter');
      f.classList.toggle('turn', i === current);
      if (i === hit) { f.classList.remove('hit'); void f.offsetWidth; f.classList.add('hit'); }
    }
  }

  async function battle() {
    const MAX_HP = 100;
    const MAX_HEAL = 3;
    const hp = [MAX_HP, MAX_HP];
    const defending = [false, false];
    const healcount = [0, 0];
    const p = [];
    let current = 0;
    hpBox.hidden = true;

    p[0] = (await input('Enter Player 1 Name: ')).trim() || 'Player 1';
    sound('rizz-sound-effect.mp3');
    p[1] = (await input('Enter Player 2 Name: ')).trim() || 'Player 2';
    sound('rizz-sound-effect.mp3');

    async function getBet(player) {
      const bet = await readInt(player + ', enter your bet: ', [['500'], ['1000'], ['5000']]);
      sound(bet < 1000 ? '999-social-credit-siren.mp3' : 'anime-wow-sound-effect.mp3');
      return bet;
    }
    const total = (await getBet(p[0])) + (await getBet(p[1]));
    showHp(p, hp, current);

    while (hp[0] > 0 && hp[1] > 0) {
      const opponent = 1 - current;
      for (let i = 0; i < 2; i++) {
        print(p[i] + ' HP: ' + hp[i] + (defending[i] ? ' (Defending)' : '') + ' | Heals left: ' + (MAX_HEAL - healcount[i]), 'dim');
      }
      print();
      print(p[current] + "'s Turn");
      print('1. Attack  2. Defend  3. Heal');
      const choice = await readInt('', [['1', 'Attack'], ['2', 'Defend'], ['3', 'Heal']]);

      if (choice === 1) {
        let damage = rand(11) + 10;
        sound(rand(2) === 0 ? '重いキック1.mp3' : 'punch-gaming-sound-effect-hd_RzlG1GE.mp3');
        if (rand(100) < 20) {
          damage *= 2;
          print('🔥 CRITICAL HIT!', 'gold');
          sound('bone-crack.mp3');
        }
        if (defending[opponent]) {
          damage = Math.trunc(damage / 2);
          defending[opponent] = false;
          sound('rizz-sound-effect.mp3');
        }
        hp[opponent] -= damage;
        print(p[current] + ' attacked for ' + damage + ' damage!', 'bad');
        showHp(p, hp, current, opponent);
      } else if (choice === 2) {
        defending[current] = true;
        print(p[current] + ' is defending!', 'good');
      } else if (choice === 3) {
        if (healcount[current] >= MAX_HEAL) {
          print('❌ No heals left!', 'bad');
          sound('fahhhhhhhhhhhhhh.mp3');
        } else {
          const heal = rand(20) + 5;
          hp[current] = Math.min(MAX_HP, hp[current] + heal);
          healcount[current]++;
          sound('anime-wow-sound-effect.mp3');
          print(p[current] + ' healed ' + heal + ' HP!', 'good');
          showHp(p, hp, current);
        }
      } else {
        print('Invalid choice!', 'bad');
        sound('vine-boom.mp3');
        continue;
      }

      if (hp[opponent] <= 0) break;
      current = opponent;
      showHp(p, hp, current);
    }

    print();
    print('===== GAME OVER =====', 'gold');
    print((hp[0] <= 0 ? p[1] : p[0]) + ' Wins!', 'gold');
    sound('rizz-sound-effect.mp3');
    print('CONGRATULATIONS YOU WIN ￥' + total, 'gold');
    print();
    print('Press ↻ Restart to play again.', 'dim');
  }

  // ---------- Teamwork02.java: bank tracker ----------
  // account.csv and support_tickets.txt are kept in memory instead of files.
  const accounts = new Map();
  const tickets = [];

  function findOrCreate(name) {
    if (!accounts.has(name)) accounts.set(name, { name, balance: 0, income: [], expense: [] });
    return accounts.get(name);
  }

  async function readDouble() {
    let prompt = 'Amount: ';
    for (;;) {
      const v = (await input(prompt)).trim();
      if (v !== '' && /^[+-]?(\d+\.?\d*|\.\d+)([eE][+-]?\d+)?[dDfF]?$/.test(v)) return parseFloat(v);
      prompt = 'Please enter a valid number: ';
    }
  }

  async function bank() {
    for (;;) {
      print('💵💵💵💵💵💵💵💵💵💵💵');
      const name = (await input('Enter your name: ')).trim().split(/\s+/)[0] || 'guest';
      print('Welcome, ' + name);
      const account = findOrCreate(name);

      let loggedIn = true;
      while (loggedIn) {
        print('------------------------', 'dim');
        print('1: Bank Account (check balance)');
        print('2: Income track (record income)');
        print('3: Expenses track (record expense)');
        print('4: Transaction history');
        print('5: Customer service');
        print('6: Logout');
        const raw = (await input('Choose an option (1-6): ', [['1', 'Balance'], ['2', 'Income'], ['3', 'Expense'], ['4', 'History'], ['5', 'Support'], ['6', 'Logout']])).trim();
        if (!/^[+-]?\d+$/.test(raw)) { print('Please enter a number.', 'bad'); continue; }

        switch (parseInt(raw, 10)) {
          case 1:
            print(name + "'s current balance: " + jd(account.balance), 'good');
            break;
          case 2: {
            const desc = await input('Income description: ');
            const amt = await readDouble();
            account.income.push(`[${today()}] ${desc}: ${jd(amt)}`);
            account.balance += amt;
            print('Income recorded. Current balance: ' + jd(account.balance), 'good');
            break;
          }
          case 3: {
            const desc = await input('Expense description: ');
            const amt = await readDouble();
            if (amt > account.balance) {
              print('Insufficient balance. Expense was not recorded.', 'bad');
            } else {
              account.expense.push(`[${today()}] ${desc}: ${jd(amt)}`);
              account.balance -= amt;
              print('Expense recorded. Current balance: ' + jd(account.balance), 'good');
            }
            break;
          }
          case 4:
            print('=== ' + name + "'s Income History ===");
            if (!account.income.length) print('(No records yet)', 'dim');
            account.income.forEach((t) => print(t, 'good'));
            print('=== ' + name + "'s Expense History ===");
            if (!account.expense.length) print('(No records yet)', 'dim');
            account.expense.forEach((t) => print(t, 'bad'));
            print('Current balance: ' + jd(account.balance));
            break;
          case 5:
            await customerService(name);
            break;
          case 6:
            loggedIn = false;
            print(name + ', you have been logged out.');
            break;
          default:
            print('Please enter a number from 1 to 6.', 'bad');
        }
      }
    }
  }

  const DELIM = '====================================';

  async function customerService(name) {
    for (;;) {
      print('--- Customer Service ---');
      print('1: Report an issue');
      print('2: View my ticket history');
      print('3: Back');
      const raw = (await input('Choose an option (1-3): ', [['1', 'Report'], ['2', 'My tickets'], ['3', 'Back']])).trim();
      if (!/^[+-]?\d+$/.test(raw)) { print('Please enter a number.', 'bad'); continue; }
      const choice = parseInt(raw, 10);
      if (choice === 1) await reportIssue(name);
      else if (choice === 2) viewMyTickets(name);
      else if (choice === 3) return;
      else print('Please enter a number from 1 to 3.', 'bad');
    }
  }

  async function reportIssue(name) {
    print('--- Select the type of issue ---');
    print('1: Account issue');
    print('2: Transaction error');
    print('3: Security concern');
    print('4: Other');
    const raw = (await input('Choose an option (1-4): ', [['1'], ['2'], ['3'], ['4']])).trim();
    const category = { 1: 'Account issue', 2: 'Transaction error', 3: 'Security concern' }[raw] || 'Other';
    const message = await input('Please describe your issue: ');
    const id = 'TCK-' + String(tickets.length + 1).padStart(4, '0');
    tickets.push([
      'Ticket ID: ' + id, 'Date: ' + now(), 'From: ' + name, 'To: support@yourbank.co.jp',
      'Subject: [Support Request] ' + category, 'Status: Open', '------------------------------------', message,
    ]);
    print('Thank you for contacting us.', 'good');
    print('Your ticket ' + id + ' has been sent to our support team.', 'good');
    print('A representative will typically respond within 24 hours.', 'good');
  }

  function viewMyTickets(name) {
    if (!tickets.length) { print('You have no support ticket history yet.', 'dim'); return; }
    const mine = tickets.filter((t) => t[2] === 'From: ' + name);
    if (!mine.length) { print(name + ' has no support ticket history yet.', 'dim'); return; }
    mine.forEach((t) => { print(DELIM, 'dim'); t.forEach((l) => print(l)); print(DELIM, 'dim'); });
  }

  // ---------- switching programs ----------
  const programs = {
    battle: { title: 'java Project1', start: battle },
    bank: { title: 'java Teamwork02', start: bank },
  };
  let active = 'battle';

  function start(name) {
    active = name;
    run++;
    waiting = null;
    out.innerHTML = '';
    chipsEl.innerHTML = '';
    promptEl.textContent = '';
    $('term-title').textContent = programs[name].title;
    document.querySelectorAll('[data-app]').forEach((b) => b.setAttribute('aria-selected', String(b.dataset.app === name)));
    document.querySelectorAll('[data-info]').forEach((s) => { s.hidden = s.dataset.info !== name; });
    print('$ ' + programs[name].title, 'dim');
    programs[name].start();
  }

  document.querySelectorAll('[data-app]').forEach((b) => b.addEventListener('click', () => start(b.dataset.app)));
  $('restart').addEventListener('click', () => start(active));
  out.addEventListener('click', () => field.focus({ preventScroll: true }));

  start('battle');
})();
