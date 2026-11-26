// Home macros "eaten X of Y" + vitamin info sheet (Hebrew)
const {chromium,devices}=require('playwright');const fs=require('fs');const path=require('path');const dir=require('path').join(__dirname,'..','app');
const html='<!doctype html><html><head><meta charset=utf8><meta name=viewport content="width=device-width,initial-scale=1"><style>body{margin:0}[hidden]{display:none!important}</style></head><body>'+fs.readFileSync(path.join(dir,'fuel-log.html'),'utf8')+'</body></html>';
const today=new Date(Date.now()-new Date().getTimezoneOffset()*60000).toISOString().slice(0,10);
(async()=>{const b=await chromium.launch();const ctx=await b.newContext({...devices['iPhone 13'],deviceScaleFactor:1});const p=await ctx.newPage();const errs=[];p.on('pageerror',e=>errs.push(e.message));
await p.route('http://app.test/**',r=>r.request().url().endsWith('foods.json')?r.fulfill({contentType:'application/json',body:fs.readFileSync(path.join(dir,'foods.json'))}):r.fulfill({contentType:'text/html',body:html}));
await p.addInitScript((today)=>{const store={'data/users/u1/profile':{weight:98,bodyFat:28,exercise:3,sex:'male',age:31,goal:'lose'},['data/users/u1/log/meals/m1']:{date:today,time:'13:00',name:'Lunch',items:[{name:'טונה',grams:100,n:{kcal:116,protein:25.5,carbs:0,fat:0.8,vitD:1.7,b12:3}}]}};
 const docRef=(pth)=>({onSnapshot:(n)=>{setTimeout(()=>n({exists:!!store[pth],data:()=>store[pth],metadata:{fromCache:false}}),10);return()=>{}},set:async(d)=>{store[pth]=d},collection:(c)=>colRef(pth+'/'+c)});
 const colRef=(pth)=>({limit:()=>({onSnapshot:(n)=>{setTimeout(()=>n({docs:Object.entries(store).filter(([k])=>k.startsWith(pth+'/')).map(([k,v])=>({id:k.split('/').pop(),exists:true,data:()=>v})),metadata:{fromCache:false}}),10);return()=>{}}}),doc:(id)=>docRef(pth+'/'+id)});
 window.claude={use:async n=>n==='db'?{doc:docRef,collection:colRef}:n==='user'?{id:async()=>'u1'}:null};},today);
await p.goto('http://app.test/');await p.waitForTimeout(600);
console.log('macros:',await p.$eval('#leftMac',e=>e.innerText.replace(/\n/g,' | ')));
await p.locator('.gapbtn').first().click();await p.waitForTimeout(150);
console.log('sheet:',(await p.$eval('.sheet',e=>e.innerText)).slice(0,420));
await p.screenshot({path:path.join(dir,'..','tests','sheet.png')});
await p.keyboard.press('Escape');console.log('closed:',await p.$eval('#sheetBg',e=>e.hidden));
await p.goto('http://app.test/#nutrition');await p.waitForTimeout(400);await p.locator('button.mc').nth(8).click();await p.waitForTimeout(100);console.log('micro opens:',await p.$eval('#shTitle',e=>e.textContent));
console.log('errors',errs);await b.close();})();
