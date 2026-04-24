# Git 分支合并和推送计划

## 当前状态
- 当前分支: 3
- 目标分支: 1（本地分支，代码不能运行）
- 远程仓库: origin/main
- 工作树状态: 干净

## 执行步骤

### 步骤 1: 切换到分支 1
```powershell
git checkout 1
```

### 步骤 2: 合并分支 3 到分支 1（保留分支 3 的代码）
由于分支 1 代码不能运行，需要尽可能保留分支 3 的代码。使用以下策略：

```powershell
# 方法1: 使用 --strategy-option=theirs 优先采用分支 3 的代码
git merge 3 -X theirs -m "Merge branch 3 into 1, keeping branch 3 changes"
```

如果出现冲突，手动解决冲突时选择保留分支 3 的代码。

### 步骤 3: 设置代理环境变量
```powershell
$env:HTTP_PROXY="http://127.0.0.1:7897"
$env:HTTPS_PROXY="http://127.0.0.1:7897"
```

验证代理是否生效：
```powershell
curl www.google.com
```

### 步骤 4: 推送到远程仓库
```powershell
git push origin 1:main
```

这将把本地分支 1 推送到远程仓库的 main 分支。

## 注意事项
1. 合并前确保分支 3 的所有更改已提交
2. 如果合并有冲突，需要手动解决
3. 推送前确保代理环境变量已正确设置
4. 推送可能需要输入 Git 凭据

## 备选方案
如果 `git merge 3 -X theirs` 不能完全解决问题，可以使用以下方法：

```powershell
# 完全用分支 3 的内容覆盖分支 1
git checkout 3
git branch -D 1
git checkout -b 1
```

然后设置代理并推送。