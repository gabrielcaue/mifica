async function main() {
  const hre = require('hardhat');
  const { ethers } = hre;
  
  console.log('Starting Badge contract deployment...');
  console.log('Network:', hre.network.name);
  
  // Check for required env vars
  const deployerKey = process.env.DEPLOYER_PRIVATE_KEY;
  const rpcUrl = process.env.POLYGON_RPC_URL || process.env.MUMBAI_RPC_URL;
  
  if (!deployerKey) {
    console.error('❌ ERROR: DEPLOYER_PRIVATE_KEY not set in environment.');
    process.exit(1);
  }
  
  if (!rpcUrl) {
    console.error('❌ ERROR: POLYGON_RPC_URL or MUMBAI_RPC_URL not set in environment.');
    process.exit(1);
  }
  
  console.log(`✓ RPC URL configured: ${rpcUrl.substring(0, 30)}...`);
  console.log(`✓ Deployer key found (length: ${deployerKey.length})`);
  
  // Get signer from key
  const deployer = new ethers.Wallet(deployerKey, ethers.provider);
  const deployerAddress = await deployer.getAddress();
  
  console.log(`\n📍 Deploying from: ${deployerAddress}`);
  
  // Get account balance
  const balance = await ethers.provider.getBalance(deployerAddress);
  const balanceInEth = ethers.formatEther(balance);
  console.log(`💰 Account balance: ${balanceInEth} MATIC`);
  
  if (balance === 0n) {
    console.warn('⚠️  WARNING: Account has 0 balance. Deployment may fail.');
  }
  
  // Deploy contract
  console.log('\n🚀 Deploying Badge contract...');
  const Badge = await ethers.getContractFactory('Badge', deployer);
  const badge = await Badge.deploy();
  
  console.log('⏳ Waiting for transaction confirmation...');
  await badge.waitForDeployment();
  
  console.log(`\n✅ SUCCESS! Badge contract deployed to: ${badge.target}`);
  console.log(`\n📝 Save this address for reference (ABI available in artifacts/contracts/Badge.sol/Badge.json)`);
}

main()
  .then(() => process.exit(0))
  .catch((error) => {
    console.error(error);
    process.exit(1);
  });
