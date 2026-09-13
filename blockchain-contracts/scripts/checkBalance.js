require('dotenv').config();
const { ethers } = require('ethers');

async function main() {
  const rpc = process.env.POLYGON_RPC_URL;
  const pk = process.env.DEPLOYER_PRIVATE_KEY;
  const minMatic = Number(process.env.MIN_MATIC || '5');

  if (!rpc) {
    console.error('POLYGON_RPC_URL not set.');
    process.exit(2);
  }

  if (!pk) {
    console.error('DEPLOYER_PRIVATE_KEY not set in environment.');
    process.exit(3);
  }

  const provider = new ethers.JsonRpcProvider(rpc);
  const wallet = new ethers.Wallet(pk, provider);

  const balance = await provider.getBalance(wallet.address);
  const matic = Number(ethers.formatEther(balance));

  console.log('Deployer address:', wallet.address);
  console.log('Balance:', matic, 'MATIC');

  if (matic < minMatic) {
    console.error(`Insufficient balance: need at least ${minMatic} MATIC (configured via MIN_MATIC)`);
    process.exit(4);
  }

  console.log('Balance check OK (>= ' + minMatic + ' MATIC).');
  process.exit(0);
}

main().catch(err => {
  console.error('Error checking balance:', err.message || err);
  process.exit(1);
});
