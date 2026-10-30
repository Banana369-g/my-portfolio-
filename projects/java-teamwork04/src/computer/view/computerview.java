package computer.view;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

import computer.entity.computer;

public class computerview {

	private final Scanner sc = new Scanner(System.in);

	private static final String STYLE = """
			<style>
			*{box-sizing:border-box}
			body{margin:0;background:#f5f5f7;color:#1d1d1f;font-family:-apple-system,BlinkMacSystemFont,'SF Pro Display','Helvetica Neue','Hiragino Sans','Yu Gothic',Meiryo,sans-serif;-webkit-font-smoothing:antialiased}
			.top{background:#f5f5f7;color:#1d1d1f;text-align:center;padding:10px;font-size:12px;border-bottom:1px solid #d2d2d7}
			.head{position:sticky;top:0;z-index:10;background:rgba(255,255,255,.8);backdrop-filter:saturate(180%) blur(20px);-webkit-backdrop-filter:saturate(180%) blur(20px);padding:0 24px;height:48px;border-bottom:1px solid #d2d2d7;display:flex;align-items:center;gap:16px}
			.logo{color:#1d1d1f;font-size:17px;font-weight:600;text-decoration:none;letter-spacing:-.01em}
			.sub{color:#6e6e73;font-size:12px}
			.wrap{max-width:1100px;margin:0 auto;padding:32px 20px 60px}
			h1{font-size:32px;font-weight:700;letter-spacing:-.02em;margin:8px 0 24px}
			.grid{display:grid;grid-template-columns:repeat(auto-fill,minmax(230px,1fr));gap:20px}
			.card{background:#fff;border-radius:18px;overflow:hidden;text-decoration:none;color:inherit;display:block;transition:transform .3s ease,box-shadow .3s ease}
			.card:hover{transform:scale(1.02);box-shadow:0 8px 24px rgba(0,0,0,.12)}
			.pic{width:100%;height:210px;object-fit:contain;display:block;background:#fff;padding:16px}
			.noimg{width:100%;height:210px;background:#fbfbfd;color:#86868b;display:flex;align-items:center;justify-content:center;font-size:13px;letter-spacing:.05em}
			.info{padding:8px 20px 24px}
			.name{font-size:17px;font-weight:600;letter-spacing:-.01em;height:2.6em;overflow:hidden;line-height:1.3}
			.brand{color:#6e6e73;font-size:13px;margin-top:4px}
			.price{color:#1d1d1f;font-size:17px;font-weight:400;margin-top:10px}
			.tag{display:inline-block;font-size:12px;margin-top:6px;color:#1d8a3a}
			.tag.low{color:#bf4800}
			.tag.out{color:#86868b}
			.detail{background:#fff;border-radius:24px;padding:40px;display:flex;flex-wrap:wrap;gap:40px}
			.detail .left{flex:1 1 320px}
			.detail .left img,.detail .left .noimg{width:100%;height:auto;max-height:440px;min-height:260px;object-fit:contain;border-radius:18px;padding:0}
			.detail .right{flex:1 1 340px}
			.detail h1{font-size:40px;margin-top:0}
			.big{color:#1d1d1f;font-size:24px;font-weight:600;margin:8px 0}
			table{border-collapse:collapse;width:100%;margin-top:16px}
			th,td{padding:12px 4px;border-bottom:1px solid #d2d2d7;text-align:left;font-size:14px}
			th{width:8em;color:#6e6e73;font-weight:400}
			.btn{display:inline-block;margin-top:24px;padding:12px 24px;border-radius:980px;background:#0071e3;color:#fff;text-decoration:none;font-size:15px;transition:background .2s;border:0;cursor:pointer;font-family:inherit}
			.btn:hover{background:#0077ed}
			.note{margin-top:16px;font-size:13px;color:#6e6e73}
			.empty{background:#fff;padding:40px;border-radius:18px;text-align:center;color:#6e6e73}
			.rating{color:#bf4800;font-size:13px;margin-top:6px}
			.reviews{margin-top:32px}
			.reviews h2{font-size:22px;margin:0 0 12px}
			.rev{border-top:1px solid #d2d2d7;padding:14px 0}
			.stars{color:#ff9f0a;font-size:16px}
			.who{color:#6e6e73;font-size:12px;margin:2px 0 6px}
			.reviews input,.reviews select,.reviews textarea{display:block;width:100%;margin:6px 0;padding:8px;border:1px solid #d2d2d7;border-radius:8px;font:inherit}
			.order{margin-top:32px;background:#f5f5f7;border-radius:18px;padding:20px}
			.order h2{font-size:22px;margin:0 0 12px}
			.order input,.order select{display:block;width:100%;margin:6px 0;padding:8px;border:1px solid #d2d2d7;border-radius:8px;font:inherit;background:#fff}
			.order .msg{margin-top:12px;font-size:14px;color:#1d8a3a}
			.ord{background:#fff;border-radius:14px;padding:16px 20px;margin-bottom:12px}
			.nav{margin-left:auto;display:flex;gap:22px}
			.nav a{color:#1d1d1f;text-decoration:none;font-size:13px}
			.nav a:hover{color:#0071e3}
			.hero{background:linear-gradient(135deg,#0071e3,#5ac8fa);color:#fff;border-radius:24px;padding:36px 32px;margin-bottom:24px}
			.hero h2{margin:0 0 16px;font-size:28px;letter-spacing:-.02em}
			.hero input{width:100%;max-width:560px;padding:14px 20px;border:0;border-radius:980px;font:inherit;font-size:16px}
			.filters{display:flex;flex-wrap:wrap;align-items:center;gap:8px;margin-bottom:12px}
			.fl{color:#6e6e73;font-size:13px;width:5.5em}
			.chip{border:1px solid #d2d2d7;background:#fff;border-radius:980px;padding:6px 16px;font:inherit;font-size:13px;cursor:pointer}
			.chip.active{background:#1d1d1f;color:#fff;border-color:#1d1d1f}
			.filters select{padding:6px 12px;border:1px solid #d2d2d7;border-radius:8px;font:inherit;font-size:13px;background:#fff}
			.rsec{background:#fff;border-radius:18px;padding:20px 24px;margin-bottom:16px}
			.rsec h3{margin:0 0 4px;font-size:18px}
			.rsec h3 a{color:inherit;text-decoration:none}
			/* ===== dark theme: black background, faint grid, blue/purple glow ===== */
			body{background:#000;color:#f5f5f7}
			body::before{content:'';position:fixed;inset:0;z-index:-1;pointer-events:none;background:radial-gradient(600px circle at 15% 8%,rgba(0,113,227,.28),transparent 60%),radial-gradient(700px circle at 88% 0%,rgba(140,80,255,.22),transparent 60%),linear-gradient(rgba(255,255,255,.04) 1px,transparent 1px),linear-gradient(90deg,rgba(255,255,255,.04) 1px,transparent 1px);background-size:auto,auto,48px 48px,48px 48px}
			.top{background:#0a0a0a;color:#a1a1a6;border-bottom:1px solid #222}
			.head{background:rgba(0,0,0,.7);border-bottom:1px solid #222}
			.logo,.nav a{color:#f5f5f7}
			.nav a:hover{color:#5ac8fa}
			.sub,.brand,.note,.who,th,.fl,.empty{color:#a1a1a6}
			.card,.detail,.rsec,.ord,.empty,.order{background:rgba(255,255,255,.04);border:1px solid rgba(255,255,255,.1)}
			.card:hover{box-shadow:0 8px 32px rgba(0,113,227,.28);border-color:rgba(90,200,250,.45)}
			.noimg{background:rgba(255,255,255,.04);color:#6e6e73}
			.price,.big{color:#f5f5f7}
			.tag{color:#30d158}
			.tag.low{color:#ff9f0a}
			.tag.out{color:#6e6e73}
			.rating{color:#ffb340}
			th,td{border-bottom:1px solid rgba(255,255,255,.12)}
			.rev{border-top:1px solid rgba(255,255,255,.12)}
			.chip{background:rgba(255,255,255,.06);color:#f5f5f7;border-color:rgba(255,255,255,.15)}
			.chip.active{background:#fff;color:#000;border-color:#fff}
			.filters select,.reviews input,.reviews select,.reviews textarea,.order input,.order select{background:#111;color:#f5f5f7;border-color:rgba(255,255,255,.2)}
			.hero{background:linear-gradient(135deg,rgba(0,113,227,.4),rgba(140,80,255,.3));border:1px solid rgba(255,255,255,.14)}
			html{scroll-behavior:smooth}
			.order{scroll-margin-top:70px}
			.btn.sm{margin-top:12px;padding:8px 20px;font-size:13px}
			.btn.off{background:#3a3a3c;color:#a1a1a6;cursor:not-allowed}
			</style>
			""";

	// 詳細ページのレビュー欄(ブラウザのlocalStorageに保存)
	private static final String REVIEW_BLOCK = """
			<div class='reviews'>
			<h2>カスタマーレビュー <span id='avg'></span></h2>
			<div id='list'></div>
			<h3>レビューを書く</h3>
			<input id='rn' placeholder='お名前'>
			<select id='rr'>
			<option value='5'>★★★★★</option><option value='4'>★★★★☆</option>
			<option value='3'>★★★☆☆</option><option value='2'>★★☆☆☆</option>
			<option value='1'>★☆☆☆☆</option>
			</select>
			<textarea id='rc' rows='3' placeholder='コメント'></textarea>
			<button class='btn' onclick='addRev()'>投稿する</button>
			</div>
			<script>
			const KEY='rev_'+__ID__;
			const SAMPLE=__SAMPLE__;
			function load(){try{return JSON.parse(localStorage.getItem(KEY))||[]}catch(e){return []}}
			function esc(s){return s.replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]))}
			function render(){
			  const a=load().concat(SAMPLE);
			  document.getElementById('avg').textContent=a.length?'★ '+(a.reduce((s,r)=>s+r.s,0)/a.length).toFixed(1)+'('+a.length+'件)':'';
			  document.getElementById('list').innerHTML=a.length?a.map(r=>'<div class="rev"><div class="stars">'+'★'.repeat(r.s)+'☆'.repeat(5-r.s)+'</div><div class="who">'+esc(r.n)+' ・ '+r.d+'</div><div>'+esc(r.c)+'</div></div>').join(''):'<div class="note">まだレビューはありません。</div>';
			}
			function addRev(){
			  const n=document.getElementById('rn').value.trim()||'匿名';
			  const c=document.getElementById('rc').value.trim();
			  if(!c){alert('コメントを入力してください');return;}
			  const a=load();
			  a.unshift({n:n,s:parseInt(document.getElementById('rr').value),c:c,d:new Date().toLocaleDateString('ja-JP')});
			  localStorage.setItem(KEY,JSON.stringify(a));
			  document.getElementById('rc').value='';
			  render();
			}
			render();
			</script>
			""";

	// 一覧ページで各商品の平均評価を表示
	private static final String RATING_SCRIPT = """
			<script>
			document.querySelectorAll('.rating').forEach(e=>{
			  let a=[];try{a=JSON.parse(localStorage.getItem('rev_'+e.dataset.id))||[]}catch(x){}
			  const sum=+e.dataset.sum+a.reduce((s,r)=>s+r.s,0), cnt=+e.dataset.cnt+a.length;
			  e.textContent=cnt?'★ '+(sum/cnt).toFixed(1)+'('+cnt+'件)':'レビューなし';
			});
			</script>
			""";

	// 詳細ページの注文フォーム(注文はブラウザのlocalStorageに保存)
	private static final String ORDER_BLOCK = """
			<div class='order' id='order'>
			<h2>ご注文</h2>
			<div id='ordbox'>
			<input id='on' placeholder='お名前'>
			<input id='ot' placeholder='電話番号'>
			<input id='oa' placeholder='お届け先の住所'>
			<input id='oq' type='number' min='1' max='__STOCK__' value='1'>
			<select id='op'><option>代金引換</option><option>コンビニ払い</option><option>銀行振込</option></select>
			<div class='note' id='ototal'></div>
			<button class='btn' onclick='placeOrder()'>注文する</button>
			</div>
			<div class='msg' id='omsg'></div>
			</div>
			<script>
			const O_PRICE=__PRICE__, O_STOCK=__STOCK__, O_NAME='__NAME__', O_ID=__ID__;
			function calc(){const q=parseInt(document.getElementById('oq').value)||0;document.getElementById('ototal').textContent='合計: ¥'+(O_PRICE*q).toLocaleString('ja-JP');}
			document.getElementById('oq').addEventListener('input',calc);calc();
			const oname=document.getElementById('on');if(oname&&location.hash==='#order')oname.focus();
			function placeOrder(){
			  const n=document.getElementById('on').value.trim();
			  const t=document.getElementById('ot').value.trim();
			  const a=document.getElementById('oa').value.trim();
			  const q=parseInt(document.getElementById('oq').value);
			  if(!n||!t||!a){alert('お名前・電話番号・住所を入力してください');return;}
			  if(!(q>=1&&q<=O_STOCK)){alert('数量は1〜'+O_STOCK+'で入力してください');return;}
			  let all=[];try{all=JSON.parse(localStorage.getItem('orders'))||[]}catch(e){}
			  const no='ORD-'+Date.now();
			  all.unshift({no:no,pid:O_ID,pn:O_NAME,q:q,total:O_PRICE*q,name:n,tel:t,addr:a,pay:document.getElementById('op').value,d:new Date().toLocaleString('ja-JP')});
			  localStorage.setItem('orders',JSON.stringify(all));
			  document.getElementById('ordbox').style.display='none';
			  document.getElementById('omsg').textContent='ご注文を受け付けました。注文番号: '+no;
			}
			</script>
			""";

	// 注文履歴ページ(orders.html)
	private static final String ORDERS_SCRIPT = """
			<script>
			function load(){try{return JSON.parse(localStorage.getItem('orders'))||[]}catch(e){return []}}
			function esc(s){return String(s).replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]))}
			function render(){
			  const a=load();
			  document.getElementById('ol').innerHTML=a.length?a.map((o,i)=>'<div class="ord"><b>'+esc(o.pn)+'</b> × '+o.q+'点 ・ 合計 ¥'+o.total.toLocaleString('ja-JP')+'<div class="note">注文番号: '+esc(o.no)+' ・ '+esc(o.d)+'</div><div class="note">お届け先: '+esc(o.name)+' / '+esc(o.tel)+' / '+esc(o.addr)+' ・ 支払い: '+esc(o.pay)+'</div><button class="btn" onclick="cancel('+i+')">注文を取り消す</button></div>').join(''):'<div class="empty">注文はまだありません。</div>';
			}
			function cancel(i){const a=load();a.splice(i,1);localStorage.setItem('orders',JSON.stringify(a));render();}
			render();
			</script>
			""";

	// 一覧ページ: 検索・絞り込み・並び替え
	private static final String FILTER_SCRIPT = """
			<script>
			let fb='',fc='',fq='';
			const g=document.querySelector('.grid');
			function apply(){
			  let n=0;
			  document.querySelectorAll('.card').forEach(e=>{
			    const ok=(!fb||e.dataset.brand===fb)&&(!fc||e.dataset.cat===fc)&&(!fq||e.dataset.key.includes(fq));
			    e.style.display=ok?'':'none';
			    if(ok)n++;
			  });
			  document.getElementById('cnt').textContent=n+'件';
			  document.getElementById('none').style.display=n?'none':'block';
			}
			document.querySelectorAll('.chip').forEach(x=>x.onclick=()=>{
			  const t=x.dataset.f;
			  document.querySelectorAll('.chip[data-f="'+t+'"]').forEach(y=>y.classList.remove('active'));
			  x.classList.add('active');
			  if(t==='brand')fb=x.dataset.v;else fc=x.dataset.v;
			  apply();
			});
			document.getElementById('q').addEventListener('input',e=>{fq=e.target.value.trim().toLowerCase();apply();});
			document.getElementById('sort').addEventListener('change',e=>{
			  if(!g)return;
			  const v=e.target.value;
			  [...g.children].sort((a,b)=>v==='asc'?a.dataset.price-b.dataset.price:v==='desc'?b.dataset.price-a.dataset.price:a.dataset.id-b.dataset.id).forEach(x=>g.appendChild(x));
			});
			</script>
			""";

	// レビュー一覧ページ(reviews.html)
	private static final String REVIEWS_SCRIPT = """
			<script>
			const P=__PRODUCTS__;
			let star=0;
			function load(id){try{return JSON.parse(localStorage.getItem('rev_'+id))||[]}catch(e){return []}}
			function esc(s){return String(s).replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]))}
			function render(){
			  let html='';
			  P.forEach(p=>{
			    const a=load(p.id).concat(p.sample).filter(r=>!star||r.s===star);
			    if(!a.length)return;
			    const avg=(a.reduce((s,r)=>s+r.s,0)/a.length).toFixed(1);
			    html+='<div class="rsec"><h3><a href="details/'+p.id+'.html">'+esc(p.name)+'</a> <span class="rating">★ '+avg+'('+a.length+'件)</span></h3>'
			      +a.map(r=>'<div class="rev"><div class="stars">'+'★'.repeat(r.s)+'☆'.repeat(5-r.s)+'</div><div class="who">'+esc(r.n)+' ・ '+r.d+'</div><div>'+esc(r.c)+'</div></div>').join('')+'</div>';
			  });
			  document.getElementById('rl').innerHTML=html||'<div class="empty">該当するレビューはありません。</div>';
			}
			document.querySelectorAll('.chip').forEach(x=>x.onclick=()=>{
			  document.querySelectorAll('.chip').forEach(y=>y.classList.remove('active'));
			  x.classList.add('active');
			  star=parseInt(x.dataset.s);
			  render();
			});
			render();
			</script>
			""";

	// サンプルレビュー(名前, 評価, コメント, 日付)。各商品に3件ずつ表示される
	private static final String[][] SAMPLE = {
			{ "田中さん", "5", "画面がとてもきれいで、動作もサクサクです。仕事にも勉強にも大満足です。", "2026/09/20" },
			{ "佐藤さん", "4", "軽くて持ち運びやすく、バッテリーも長持ちします。価格の割に性能が高いと思います。", "2026/09/14" },
			{ "鈴木さん", "5", "届いてすぐに使えました。梱包も丁寧で、出品者の対応も早かったです。", "2026/09/08" },
			{ "高橋さん", "3", "性能は問題ありませんが、ファンの音が少し気になります。それ以外は満足です。", "2026/08/30" },
			{ "伊藤さん", "4", "プログラミングの勉強用に購入しました。複数のアプリを開いても快適です。", "2026/08/25" },
			{ "渡辺さん", "5", "動画編集でも重くならず、買ってよかったです。おすすめです。", "2026/08/17" },
			{ "山本さん", "2", "商品自体は良いのですが、配送が予定より遅れました。", "2026/08/09" },
			{ "中村さん", "4", "デザインがおしゃれで、キーボードも打ちやすいです。", "2026/07/28" } };

	private static final int SAMPLE_COUNT = 3;

	private int[] sampleIdx(int id) {
		int[] idx = new int[SAMPLE_COUNT];
		for (int k = 0; k < SAMPLE_COUNT; k++) {
			idx[k] = (id + k * 3) % SAMPLE.length;
		}
		return idx;
	}

	private String sampleJson(int id) {
		StringBuilder sb = new StringBuilder("[");
		for (int i : sampleIdx(id)) {
			String[] r = SAMPLE[i];
			if (sb.length() > 1) {
				sb.append(",");
			}
			sb.append("{n:'").append(r[0]).append("',s:").append(r[1])
					.append(",c:'").append(r[2]).append("',d:'").append(r[3]).append("'}");
		}
		return sb.append("]").toString();
	}

	private int sampleSum(int id) {
		int sum = 0;
		for (int i : sampleIdx(id)) {
			sum += Integer.parseInt(SAMPLE[i][1]);
		}
		return sum;
	}

	public void showMenu() {
		System.out.println("===== パソコン売買サイト =====");
		System.out.println("1: 一覧表示");
		System.out.println("2: 詳細表示");
		System.out.println("3: キーワード検索");
		System.out.println("4: 価格検索");
		System.out.println("5: 購入する(買う)");
		System.out.println("6: 出品する(売る)");
		System.out.println("0: 終了");
		System.out.print("選択してください: ");
	}

	public int readMenu() {
		return readInt();
	}

	public int readInt() {
		while (!sc.hasNextInt()) {
			System.out.print("数字を入力してください: ");
			sc.next();
		}
		int v = sc.nextInt();
		sc.nextLine();
		return v;
	}

	public long readLong() {
		while (!sc.hasNextLong()) {
			System.out.print("数字を入力してください: ");
			sc.next();
		}
		long v = sc.nextLong();
		sc.nextLine();
		return v;
	}

	public String readString() {
		return sc.nextLine();
	}

	public void showMessage(String msg) {
		System.out.println(msg);
	}

	public void showList(List<computer> list) {

		try {
			new File("details").mkdirs();

			FileWriter fw = new FileWriter("computerList.html", StandardCharsets.UTF_8);

			fw.write("<!DOCTYPE html><html lang='ja'><head><meta charset='UTF-8'>");
			fw.write("<title>パソコン売買サイト</title>" + STYLE + "</head><body>");
			fw.write(header(""));
			fw.write("<div class='wrap'>");
			fw.write(toolbar(list));
			fw.write("<h1>おすすめのパソコン <span class='sub' id='cnt'>" + list.size() + "件</span></h1>");

			if (list.isEmpty()) {
				fw.write("<div class='empty'>該当する商品が見つかりません。</div>");
			} else {
				fw.write("<div class='grid'>");
				for (computer c : list) {
					fw.write("<a class='card' href='details/" + c.getComputerId() + ".html' data-id='"
							+ c.getComputerId()
							+ "' data-brand='" + esc(c.getBrand()) + "' data-cat='" + esc(c.getCategory())
							+ "' data-price='" + c.getPrice() + "' data-key='"
							+ esc((c.getName() + " " + c.getBrand() + " " + c.getCpu() + " " + c.getGpu() + " "
									+ c.getCategory()).toLowerCase())
							+ "'>");
					fw.write(picture(c, ""));
					fw.write("<div class='info'>");
					fw.write("<div class='name'>" + esc(c.getName()) + "</div>");
					fw.write("<div class='brand'>" + esc(c.getBrand()) + " / " + esc(c.getCategory()) + "</div>");
					fw.write("<div class='price'>" + yen(c.getPrice()) + "</div>");
					fw.write("<div class='rating' data-id='" + c.getComputerId() + "' data-sum='"
							+ sampleSum(c.getComputerId()) + "' data-cnt='" + SAMPLE_COUNT + "'></div>");
					fw.write(stockTag(c.getQuantity()));
					fw.write(
							"<div><span class='btn sm' onclick=\"event.preventDefault();event.stopPropagation();location.href='details/"
									+ c.getComputerId() + ".html#order'\">注文する</span></div>");
					fw.write("</div></a>");

					writeDetail(c);
				}
				fw.write("</div>");
			}

			fw.write("</div>");
			fw.write(RATING_SCRIPT);
			fw.write(FILTER_SCRIPT);
			fw.write("</body></html>");
			fw.close();

			writeOrdersPage();
			writeReviewsPage(list);
			System.out.println("computerList.htmlを作成しました");

			Desktop.getDesktop().browse(new File("computerList.html").toURI());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void showDetail(computer c) {

		if (c == null) {
			System.out.println("該当する商品が見つかりません。");
			return;
		}

		try {
			writeDetail(c);
			writeOrdersPage();
			Desktop.getDesktop().browse(new File("details/" + c.getComputerId() + ".html").toURI());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void writeDetail(computer c) throws Exception {

		new File("details").mkdirs();

		FileWriter fw = new FileWriter("details/" + c.getComputerId() + ".html", StandardCharsets.UTF_8);

		fw.write("<!DOCTYPE html><html lang='ja'><head><meta charset='UTF-8'>");
		fw.write("<title>" + esc(c.getName()) + "</title>" + STYLE + "</head><body>");
		fw.write(header("../"));
		fw.write("<div class='wrap'><div class='detail'>");

		// 詳細ページは details/ フォルダの中なので "../" を付ける
		fw.write("<div class='left'>" + picture(c, "../") + "</div>");

		fw.write("<div class='right'>");
		fw.write("<h1>" + esc(c.getName()) + "</h1>");
		fw.write("<div class='big'>" + yen(c.getPrice()) + "</div>");
		fw.write(stockTag(c.getQuantity()));
		fw.write("<div><a class='btn' href='#order'>注文する</a></div>");

		fw.write("<table>");
		row(fw, "商品ID", String.valueOf(c.getComputerId()));
		row(fw, "ブランド", esc(c.getBrand()));
		row(fw, "CPU", esc(c.getCpu()));
		row(fw, "GPU", esc(c.getGpu()));
		row(fw, "RAM", c.getRam() + "GB");
		row(fw, "ストレージ", c.getStorage() + "GB");
		row(fw, "カテゴリ", esc(c.getCategory()));
		row(fw, "説明", esc(c.getDescription()));
		if (c.getQuantity() > 0) {
			row(fw, "在庫", c.getQuantity() + "点");
		}
		row(fw, "出品者", esc(c.getSeller()));
		fw.write("</table>");

		fw.write("<a class='btn' href='../computerList.html'>一覧に戻る</a>");
		fw.write("<div class='note'>購入する場合は、コンソールのメニュー5で商品ID「" + c.getComputerId() + "」を入力してください。</div>");

		fw.write(ORDER_BLOCK.replace("__ID__", String.valueOf(c.getComputerId()))
				.replace("__NAME__", jsStr(c.getName()))
				.replace("__PRICE__", String.valueOf(c.getPrice()))
				.replace("__STOCK__", String.valueOf(c.getQuantity() > 0 ? c.getQuantity() : 99)));

		fw.write(REVIEW_BLOCK.replace("__ID__", String.valueOf(c.getComputerId()))
				.replace("__SAMPLE__", sampleJson(c.getComputerId())));
		fw.write("</div>");

		fw.write("</div></div></body></html>");
		fw.close();
	}

	private void writeOrdersPage() throws Exception {
		FileWriter fw = new FileWriter("orders.html", StandardCharsets.UTF_8);
		fw.write("<!DOCTYPE html><html lang='ja'><head><meta charset='UTF-8'>");
		fw.write("<title>注文履歴</title>" + STYLE + "</head><body>");
		fw.write(header(""));
		fw.write("<div class='wrap'><h1>注文履歴</h1><div id='ol'></div></div>");
		fw.write(ORDERS_SCRIPT);
		fw.write("</body></html>");
		fw.close();
	}

	private String jsStr(String s) {
		if (s == null) {
			return "";
		}
		return s.replace("'", "").replace("\\", "").replace("<", "").replace("\n", " ");
	}

	private String toolbar(List<computer> list) {
		java.util.Set<String> brands = new java.util.TreeSet<>();
		java.util.Set<String> cats = new java.util.TreeSet<>();
		for (computer c : list) {
			if (c.getBrand() != null && !c.getBrand().isBlank()) {
				brands.add(c.getBrand());
			}
			if (c.getCategory() != null && !c.getCategory().isBlank()) {
				cats.add(c.getCategory());
			}
		}
		StringBuilder sb = new StringBuilder();
		sb.append("<div class='hero'><h2>欲しいパソコンを探そう</h2>");
		sb.append("<input id='q' type='search' placeholder='商品名・ブランド・CPUで検索'></div>");
		sb.append(chips("ブランド", "brand", brands));
		sb.append(chips("カテゴリ", "cat", cats));
		sb.append("<div class='filters'><span class='fl'>並び替え</span><select id='sort'>"
				+ "<option value='id'>おすすめ順</option><option value='asc'>価格が安い順</option>"
				+ "<option value='desc'>価格が高い順</option></select></div>");
		sb.append("<div class='empty' id='none' style='display:none'>該当する商品が見つかりません。</div>");
		return sb.toString();
	}

	private String chips(String label, String type, java.util.Set<String> values) {
		StringBuilder sb = new StringBuilder("<div class='filters'><span class='fl'>" + label + "</span>");
		sb.append("<button class='chip active' data-f='" + type + "' data-v=''>すべて</button>");
		for (String v : values) {
			sb.append("<button class='chip' data-f='" + type + "' data-v='" + esc(v) + "'>" + esc(v) + "</button>");
		}
		return sb.append("</div>").toString();
	}

	private void writeReviewsPage(List<computer> list) throws Exception {
		StringBuilder sb = new StringBuilder("[");
		for (computer c : list) {
			if (sb.length() > 1) {
				sb.append(",");
			}
			sb.append("{id:").append(c.getComputerId()).append(",name:'").append(jsStr(c.getName()))
					.append("',sample:").append(sampleJson(c.getComputerId())).append("}");
		}
		sb.append("]");

		FileWriter fw = new FileWriter("reviews.html", StandardCharsets.UTF_8);
		fw.write("<!DOCTYPE html><html lang='ja'><head><meta charset='UTF-8'>");
		fw.write("<title>カスタマーレビュー</title>" + STYLE + "</head><body>");
		fw.write(header(""));
		fw.write("<div class='wrap'><h1>カスタマーレビュー</h1>");
		fw.write("<div class='filters'><span class='fl'>評価</span><button class='chip active' data-s='0'>すべて</button>");
		for (int n = 5; n >= 1; n--) {
			fw.write("<button class='chip' data-s='" + n + "'>★" + n + "</button>");
		}
		fw.write("</div><div id='rl'></div></div>");
		fw.write(REVIEWS_SCRIPT.replace("__PRODUCTS__", sb.toString()));
		fw.write("</body></html>");
		fw.close();
	}

	private String header(String prefix) {
		return "<div class='top'>パソコンをお得に売買しよう</div>"
				+ "<div class='head'><a class='logo' href='" + prefix + "computerList.html'>パソコン売買サイト</a>"
				+ "<nav class='nav'>"
				+ "<a href='" + prefix + "computerList.html'>ホーム</a>"
				+ "<a href='" + prefix + "reviews.html'>レビュー</a>"
				+ "<a href='" + prefix + "orders.html'>注文履歴</a>"
				+ "</nav></div>";
	}

	private String picture(computer c, String prefix) {
		String img = c.getImage();
		if (img == null || img.isBlank()) {
			return "<div class='noimg'>NO IMAGE</div>";
		}
		String src = img.startsWith("http") ? img : prefix + img;
		return "<img class='pic' src='" + esc(src) + "' alt='" + esc(c.getName()) + "'>";
	}

	private String stockTag(int qty) {
		if (qty <= 0) {
			return "";
		}
		if (qty <= 2) {
			return "<span class='tag low'>残り" + qty + "点</span>";
		}
		return "<span class='tag'>在庫あり</span>";
	}

	private String yen(long price) {
		return "¥" + String.format("%,d", price);
	}

	private void row(FileWriter fw, String label, String value) throws Exception {
		fw.write("<tr><th>" + label + "</th><td>" + value + "</td></tr>");
	}

	private String esc(String s) {
		if (s == null) {
			return "";
		}
		return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
				.replace("\"", "&quot;").replace("'", "&#39;");
	}
}