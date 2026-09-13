require('dotenv').config();
require('@nomicfoundation/hardhat-toolbox');

const { POLYGON_RPC_URL, MUMBAI_RPC_URL } = process.env;

module.exports = {
  solidity: '0.8.19',
  networks: {
    mumbai: {
      url: MUMBAI_RPC_URL || '',
      accounts: process.env.DEPLOYER_PRIVATE_KEY ? [process.env.DEPLOYER_PRIVATE_KEY] : []
    },
    polygon: {
      url: POLYGON_RPC_URL || '',
      accounts: process.env.DEPLOYER_PRIVATE_KEY ? [process.env.DEPLOYER_PRIVATE_KEY] : []
    }
  }
};
