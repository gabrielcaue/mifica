import fs from 'node:fs/promises';
import path from 'node:path';
import { pathToFileURL } from 'node:url';
import puppeteer from 'puppeteer';

const [, , inputArg, outputBaseArg] = process.argv;

if (!inputArg || !outputBaseArg) {
  console.error('Usage: node render-bpmn.mjs <input.bpmn> <output-base-path>');
  process.exit(1);
}

const inputPath = path.resolve(process.cwd(), inputArg);
const outputBase = path.resolve(process.cwd(), outputBaseArg);
const outputPng = `${outputBase}.png`;
const outputSvg = `${outputBase}.svg`;

const bpmnXml = await fs.readFile(inputPath, 'utf8');

const html = `<!doctype html>
<html>
<head>
  <meta charset="utf-8" />
  <style>
    html, body, #canvas { margin: 0; width: 100%; height: 100%; overflow: hidden; background: #fff; }
  </style>
</head>
<body>
  <div id="canvas"></div>
</body>
</html>`;

const browser = await puppeteer.launch({ headless: true });
const page = await browser.newPage({ viewport: { width: 2200, height: 1200, deviceScaleFactor: 2 } });

await page.setContent(html, { waitUntil: 'networkidle0' });

const bpmnViewerPath = path.resolve(process.cwd(), 'node_modules/bpmn-js/dist/bpmn-viewer.production.min.js');
await page.addScriptTag({ path: bpmnViewerPath });

await page.evaluate((xml) => {
  try {
    const viewer = new window.BpmnJS({ container: '#canvas' });
    viewer.importXML(xml)
      .then(() => {
        const canvas = viewer.get('canvas');
        canvas.zoom('fit-viewport');
        window.__ready = true;
        window.__viewer = viewer;
      })
      .catch((e) => {
        window.__error = String((e && e.message) || e);
      });
  } catch (e) {
    window.__error = String((e && e.message) || e);
  }
}, bpmnXml);

await page.waitForFunction(() => window.__ready === true || !!window.__error, { timeout: 30000 });

const err = await page.evaluate(() => window.__error || null);
if (err) {
  await browser.close();
  throw new Error(`Failed to render BPMN: ${err}`);
}

const svg = await page.evaluate(async () => {
  const result = await window.__viewer.saveSVG();
  return result.svg;
});

await fs.writeFile(outputSvg, svg, 'utf8');
await page.screenshot({ path: outputPng, fullPage: true });

await browser.close();

console.log(`Generated: ${outputPng}`);
console.log(`Generated: ${outputSvg}`);
console.log(`Opened source: ${pathToFileURL(inputPath).href}`);
