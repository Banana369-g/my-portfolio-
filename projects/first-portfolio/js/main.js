// スクロールでヘッダーの背景とページトップボタンを切り替える
const header = document.querySelector(".site-header");
const pagetop = document.querySelector(".pagetop");

window.addEventListener("scroll", () => {
	header.classList.toggle("scrolled", window.scrollY > 40);
	pagetop.classList.toggle("show", window.scrollY > 500);
});

// スマホ用メニューの開閉
const menuBtn = document.querySelector(".menu-btn");
const navbar = document.querySelector(".navbar");

menuBtn.addEventListener("click", () => {
	const open = navbar.classList.toggle("open");
	menuBtn.setAttribute("aria-expanded", open);
});

navbar.querySelectorAll("a").forEach((link) => {
	link.addEventListener("click", () => navbar.classList.remove("open"));
});

// 画面に入った要素をふわっと表示する
const revealObserver = new IntersectionObserver((entries) => {
	entries.forEach((entry) => {
		if (entry.isIntersecting) {
			entry.target.classList.add("visible");
			revealObserver.unobserve(entry.target);
		}
	});
}, { threshold: 0.15 });

document.querySelectorAll(".reveal").forEach((el) => revealObserver.observe(el));

// プロジェクトのコードをファイルごとにタブで切り替える
document.querySelectorAll(".code-tab").forEach((tab) => {
	tab.addEventListener("click", () => {
		const details = tab.closest("details");
		details.querySelectorAll(".code-tab, .code-file").forEach((el) => el.classList.remove("active"));
		tab.classList.add("active");
		document.getElementById(tab.dataset.file).classList.add("active");
	});
});

// 今見ているセクションのメニューを光らせる
const navLinks = navbar.querySelectorAll("a");

const sectionObserver = new IntersectionObserver((entries) => {
	entries.forEach((entry) => {
		if (entry.isIntersecting) {
			navLinks.forEach((link) => {
				link.classList.toggle("active", link.getAttribute("href") === "#" + entry.target.id);
			});
		}
	});
}, { rootMargin: "-45% 0px -50% 0px" });

document.querySelectorAll("main section[id]").forEach((section) => sectionObserver.observe(section));
