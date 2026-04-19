# 商家端页面Vue DevTools元素隐藏 Spec

## Why
商家端登录页面已成功隐藏Vue DevTools悬浮面板元素，但其他商家端页面可能仍存在相同问题。需要统一处理所有商家端页面，确保页面干净整洁，提升用户体验。

## What Changes
- 检查所有商家端页面文件（21个）
- 为每个页面添加Vue DevTools元素隐藏CSS规则
- 统一隐藏样式规则，确保一致性

## Impact
- Affected specs: 商家端所有页面
- Affected code: `cg-vue/src/views/merchant/` 目录下所有Vue文件

## ADDED Requirements

### Requirement: Vue DevTools元素隐藏
所有商家端页面 SHALL 隐藏Vue DevTools扩展添加的悬浮面板元素。

#### Scenario: 页面加载
- **WHEN** 用户访问任意商家端页面
- **THEN** 页面不显示Vue DevTools悬浮面板
- **AND** 页面其他功能正常

#### Scenario: 开发调试
- **WHEN** 开发者需要使用Vue DevTools
- **THEN** 可通过浏览器扩展管理界面临时启用
- **AND** 不影响生产环境页面展示

## MODIFIED Requirements
无修改需求。

## REMOVED Requirements
无移除需求。
