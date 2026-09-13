require('dotenv').config();

async function main() {
  const rpcUrl = process.env.POLYGON_RPC_URL || process.env.MUMBAI_RPC_URL;
  if (!rpcUrl) {
    console.error('Missing RPC URL. Set POLYGON_RPC_URL or MUMBAI_RPC_URL in env.');
    process.exit(1);
  }

  // Provider (Hardhat exposes a provider when running via npx hardhat)
  let provider = ethers.provider;
  if (!provider) {
    provider = new ethers.providers.JsonRpcProvider(rpcUrl);
  }

  let deployerSigner;
  if (process.env.DEPLOYER_PRIVATE_KEY) {
    deployerSigner = new ethers.Wallet(process.env.DEPLOYER_PRIVATE_KEY, provider);
    console.log('Using deployer from DEPLOYER_PRIVATE_KEY:', await deployerSigner.getAddress());
  } else {
    const signers = await ethers.getSigners();
    if (signers && signers.length > 0) {
      deployerSigner = signers[0];
      console.log('Using first Hardhat signer:', await deployerSigner.getAddress());
    } else {
      console.error('No deployer available: set DEPLOYER_PRIVATE_KEY or run with a Hardhat network that provides signers.');
      process.exit(1);
    }
  }

  console.log('Deploying contracts on network with RPC:', rpcUrl);

  const Badge = await ethers.getContractFactory('Badge', deployerSigner);
  const badge = await Badge.deploy();
  await badge.deployed();

  console.log('Badge deployed to:', badge.address);
}

main()
  .then(() => process.exit(0))
  .catch((error) => {
    console.error(error);
    process.exit(1);
  });
