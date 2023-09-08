# 屏幕取样  
> 下载：[ScreenSnap/releases](https://github.com/Edsuns/ScreenSnap/releases)

### 功能概览  
> 兼容多平台,兼容多版本JDK(已测试JDK8~JDK17)
- 屏幕取色
- 屏幕二维码扫描
- 屏幕截图

### 用户指南  
> 启动：拖出压缩包里的文件夹，双击文件夹里的“run.bat”运行程序

__1. 选取操作__  
- 左键单击 -> 屏幕取色
- 左键拖动圈选 -> 截图
- 右键拖动圈选 -> 二维码扫描
- 右键单击 -> 取消
- 键盘方向键 -> 单像素移动鼠标
- 键盘回车或空格 -> 确认取色

__2. 截图预览__  
- 鼠标滚轮 -> 缩放大小
- 左键拖动 -> 拖动图片或窗口
- 左键双击 -> 切换窗口最大化
- 显示比例、旋转、保存、复制等按钮

__3. 取色复制窗口__  
- 点击颜色标签可重新取色
- 点击右侧复制按钮可复制相应颜色

### 问题解决  
- __截图界面错位或模糊：__  
添加VM配置 `-Dsun.java2d.uiScale=1`

- __Windows下中文乱码：__  
添加VM配置 `-Dfile.encoding=GBK`

- __resources加载出错：__  
在Artifacts里把resources添加到Directory Content

- __程序卡住：__  
选中Windows自带控制台的内容时，`System.out`会被阻塞，导致程序卡住。右键控制台，让程序继续运行  

- __JDK9及以上截图报错：__
添加VM配置 `--add-exports=java.desktop/sun.awt=ALL-UNNAMED --add-exports=java.desktop/java.awt.peer=ALL-UNNAMED`