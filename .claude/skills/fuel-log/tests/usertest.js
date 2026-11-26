const {chromium,devices}=require('playwright');const fs=require('fs');const path=require('path');const dir=require('path').join(__dirname,'..','app');
const html='<!doctype html><html><head><meta charset=utf8><meta name=viewport content="width=device-width,initial-scale=1"><style>body{margin:0}[hidden]{display:none!important}</style></head><body>'+fs.readFileSync(path.join(dir,'fuel-log.html'),'utf8')+'</body></html>';
(async()=>{const b=await chromium.launch();
for (const uid of ['u_owner',null]){
const ctx=await b.newContext({...devices['iPhone 13'],deviceScaleFactor:1});const p=await ctx.newPage();const errs=[];p.on('pageerror',e=>errs.push(e.message));
await p.route('http://app.test/**',r=>r.request().url().endsWith('foods.json')?r.fulfill({contentType:'application/json',body:fs.readFileSync(path.join(dir,'foods.json'))}):r.fulfill({contentType:'text/html',body:html}));
await p.addInitScript((uid)=>{window.__paths=[];const store={};
 const docRef=(pth)=>({path:pth,onSnapshot:(n)=>{window.__paths.push('sub doc '+pth);setTimeout(()=>n({exists:!!store[pth],data:()=>store[pth],metadata:{fromCache:false}}),10);return()=>{}},set:async(d)=>{window.__paths.push('set '+pth);store[pth]=d},delete:async()=>{},collection:(c)=>colRef(pth+'/'+c)});
 const colRef=(pth)=>({limit:()=>({onSnapshot:(n)=>{window.__paths.push('sub col '+pth);setTimeout(()=>n({docs:Object.entries(store).filter(([k])=>k.startsWith(pth+'/')).map(([k,v])=>({id:k.split('/').pop(),exists:true,data:()=>v})),metadata:{fromCache:false}}),10);return()=>{}}}),doc:(id)=>docRef(pth+'/'+id)});
 const db={doc:docRef,collection:colRef};
 const user={id:async()=>uid,can:async()=>false};
 window.claude={use:async n=>n==='db'?db:n==='user'?user:null};},uid);
await p.goto('http://app.test/#me');await p.waitForTimeout(500);
if(uid){ await p.fill('#pWeight','80');await p.fill('#pFat','18');await p.fill('#pEx','4');await p.click('#profSave');await p.waitForTimeout(200); }
console.log(uid, JSON.stringify(await p.evaluate(()=>window.__paths)), 'status:',await p.$eval('#status',e=>e.textContent), 'saveDisabled:',await p.$eval('#profSave',e=>e.disabled), errs);
await ctx.close();}
await b.close();})();
