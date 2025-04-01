// Refactoring.test.js
const { statement } = require('./Refactoring2');
const test = require('node:test');
const assert = require('node:assert');

test('기본 청구서 출력 테스트', () => {
    const invoice = {
        customer: '고객A',
        performances: [
            { playID: 'hamlet', audience: 55 },
            { playID: 'as-like', audience: 35 },
        ],
    };

    const plays = {
        hamlet: { name: '햄릿', type: 'tragedy' },
        'as-like': { name: '뜻대로 하세요', type: 'comedy' },
    };

    const result = statement(invoice, plays);

    console.log(result); // 확인용

    assert.ok(result.includes('고객명: 고객A'), '고객명이 포함되어야 함');
    assert.ok(result.includes('햄릿: $'), '햄릿 가격이 포함되어야 함');
    assert.ok(result.includes('뜻대로 하세요: $'), '뜻대로 하세요 가격이 포함되어야 함');
    assert.ok(result.includes('총액:'), '총액이 포함되어야 함');
    assert.ok(result.includes('적립 포인트:'), '적립 포인트가 포함되어야 함');
});