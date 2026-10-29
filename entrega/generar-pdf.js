// Genera entrega/PSP-UD2-cuaderno.pdf a partir de entrega/cuaderno.html
const { chromium } = require('playwright');
const path = require('path');
(async () => {
  const browser = await chromium.launch();
  const page = await browser.newPage();
  await page.goto('file://' + path.join(__dirname, 'cuaderno.html'));
  await page.pdf({ path: path.join(__dirname, 'PSP-UD2-cuaderno.pdf'), format: 'A4', printBackground: true });
  await browser.close();
})();
