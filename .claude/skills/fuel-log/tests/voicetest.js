const {chromium,devices}=require('playwright');const fs=require('fs');const path=require('path');const dir=require('path').join(__dirname,'..','app');
const html='<!doctype html><html><head><meta charset=utf8><meta name=viewport content="width=device-width,initial-scale=1"><style>body{margin:0}[hidden]{display:none!important}</style></head><body>'+fs.readFileSync(path.join(dir,'fuel-log.html'),'utf8')+'</body></html>';
(async()=>{const b=await chromium.launch();
for (const withDict of [true,false]){
const ctx=await b.newContext({...devices['iPhone 13'],deviceScaleFactor:1});const p=await ctx.newPage();const errs=[];p.on('pageerror',e=>errs.push(e.message));
await p.route('http://app.test/**',r=>r.request().url().endsWith('foods.json')?r.fulfill({contentType:'application/json',body:fs.readFileSync(path.join(dir,'foods.json'))}):r.fulfill({contentType:'text/html',body:html}));
await p.addInitScript((withDict)=>{window.__w=[];const prof={weight:80,bodyFat:18,exercise:4,sex:'male',age:30,goal:'maintain'};
 const db={doc:()=>({onSnapshot:(n)=>{setTimeout(()=>n({exists:true,data:()=>prof,metadata:{fromCache:false}}),10);return()=>{}}}),collection:(c)=>({limit:()=>({onSnapshot:(n)=>{setTimeout(()=>n({docs:[],metadata:{fromCache:false}}),10);return()=>{}}}),doc:(id)=>({set:async(d)=>{window.__w.push([c,id,d])},delete:async()=>{window.__w.push(['del',id])}})})};
 const sample=async()=>({});sample.json=async(pr)=>pr.includes('"ideas"')?{ideas:[]}:({meal:'Dinner',time:null,items:[{name:'ביצים',amount:'4 ביצים',grams:200,foodId:'egg'},{name:'לחמנייה',amount:'100 ג׳',grams:100,foodId:'roll'},{name:'רוטב עמבה',amount:'כף',grams:15,foodId:null,n:{kcal:20,protein:0.1,carbs:4,fat:0.2}}]});
 const h={};const dict={start:async(o)=>{window.__lang=o;h.s({state:'listening'});setTimeout(()=>h.f({text:'בארוחת ערב אכלתי 4 ביצים ולחמנייה עם עמבה'}),50)},stop:async()=>{h.s({state:'idle',reason:'stopped'})},onPartial:f=>{h.p=f;return()=>{}},onFinal:f=>{h.f=f;return()=>{}},onState:f=>{h.s=f;f({state:'idle'});return()=>{}}};
 const _d=db.doc; db.doc=(p)=>Object.assign(_d(p),{collection:db.collection}); const user={id:async()=>'u_test',can:async()=>false};
 window.claude={use:async n=>n==='user'?user:n==='db'?db:n==='sample'?sample:(n==='dictation'&&withDict)?dict:null};},withDict);
await p.goto('http://app.test/');await p.waitForTimeout(500);
await p.screenshot({path:path.join(dir,withDict?'rec-home.png':'rec-home-nodict.png')});
await p.click('#qRec');await p.waitForTimeout(300);if(!withDict){await p.fill('#aiText','4 ביצים ולחמנייה');await p.press('#aiText','Enter');await p.waitForTimeout(300);console.log('enter-logged:',await p.evaluate(()=>window.__w.filter(x=>x[0]==='meals').length));await p.screenshot({path:path.join(dir,'ios.png')});}
if(withDict){ await p.click('#aiMic');await p.waitForTimeout(400);
  const r=await p.evaluate(()=>({lang:window.__lang,w:window.__w.map(x=>x[0]==='meals'?['meals',x[2].name,x[2].time,x[2].date,x[2].items.map(i=>i.name+':'+Math.round(i.n.kcal)).join('|')]:[x[0],x[1]]),logged:document.querySelector('.logged')&&document.querySelector('.logged').innerText}));
  console.log(JSON.stringify(r,null,1)); await p.screenshot({path:path.join(dir,'rec-done.png')});
} else { console.log('nodict: mic hidden=',await p.$eval('#aiMic',e=>e.hidden),'hint shown=',!(await p.$eval('#micHint',e=>e.hidden)),'focused=',await p.evaluate(()=>document.activeElement.id)); }
console.log('errors',errs);await ctx.close();}
await b.close();})();
