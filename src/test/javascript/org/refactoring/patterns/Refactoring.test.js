const { statement } = require('./Refactoring2.base');
const test = require('node:test');
const assert = require('node:assert');

// 테스트용 데이터 정의
const plays = {
    "hamlet": { "name": "Hamlet", "type": "tragedy" },
    "as-like": { "name": "As You Like It", "type": "comedy" },
    "othello": { "name": "Othello", "type": "tragedy" }
};

const invoices = [
    {
        "customer": "BigCo",
        "performances": [
            { "playID": "hamlet", "audience": 55 },
            { "playID": "as-like", "audience": 35 },
            { "playID": "othello", "audience": 40 }
        ]
    }
];

test('BigCo 고객의 청구서 출력 테스트', () => {
    const invoice = invoices[0];
    const result = statement(invoice, plays);

    console.log('실제 출력:\n' + result);

    assert.ok(result.includes('청구 내역 (고객명: BigCo)'), '고객명이 포함되어야 함');
    assert.ok(result.includes('Hamlet: $650.00 (55석)'), 'Hamlet 공연 정보가 정확해야 함');
    assert.ok(result.includes('As You Like It: $580.00 (35석)'), 'As You Like It 공연 정보가 정확해야 함');
    assert.ok(result.includes('Othello: $500.00 (40석)'), 'Othello 공연 정보가 정확해야 함');
    assert.ok(result.includes('총액: $1,730.00'), '총액이 정확해야 함');
    assert.ok(result.includes('적립 포인트: 47점'), '적립 포인트가 정확해야 함');
});