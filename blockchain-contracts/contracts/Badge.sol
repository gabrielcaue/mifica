// SPDX-License-Identifier: MIT
pragma solidity ^0.8.19;

contract Badge {
    event BadgeCreated(address indexed creator, address indexed to, uint256 id, string metadata);

    uint256 public nextId;

    function createBadge(address to, string calldata metadata) external {
        uint256 id = ++nextId;
        emit BadgeCreated(msg.sender, to, id, metadata);
    }
}
