/** @type {import('jest').Config} */
module.exports = {
  preset: "ts-jest",
  testEnvironment: "node",
  testMatch: ["<rootDir>/test/**/*.test.ts"],
  collectCoverageFrom: ["src/**/*.ts"],
  moduleFileExtensions: ["ts", "js"],
};
