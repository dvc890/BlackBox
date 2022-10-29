![xx](assets/banner.png)
### [English Version](README_EN.md)

# 虚拟引擎 · BlackBox
> The only people who have anything to fear from free software are those whose products are worth even less. 
>
> <p align="right">——David Emery</p>

![](https://img.shields.io/badge/language-java-brightgreen.svg)

黑盒BlackBox，是一款虚拟引擎，可以在Android上克隆、运行虚拟应用，拥有免安装运行能力。黑盒可以掌控被运行的虚拟应用，做任何想做的事情。

## 交流
[Telegram](https://t.me/blackboxing)

## 支持
暂不考虑4x，目前已兼容 5.0 ～ 12.0并跟进后续新系统。

如果条件允许，降级targetSdkVersion到28或以下可以获得更好的兼容性。

***稳定性未经大量测试，仅供学习交流，请勿用于其他用途***

## 架构说明
本项目区分32位与64位，目前是2个不同的app，如在Demo已安装列表内无法找到需要开启的app说明不支持，请编译其他的架构。

## 如何使用
### Step 1.初始化，在Application中加入以下代码初始化

```java
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        try {
            BlackBoxCore.get().doAttachBaseContext(base, new ClientConfiguration() {
                @Override
                public String getHostPackageName() {
                    return base.getPackageName();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        BlackBoxCore.get().doCreate();
    }
```

### Step 2.安装应用至黑盒内
```java
    // 已安装的应用可以提供包名
    BlackBoxCore.get().installPackageAsUser("com.tencent.mm", userId);
    
    // 未安装的应用可以提供路径
    BlackBoxCore.get().installPackageAsUser(new File("/sdcard/com.tencent.mm.apk"), userId);
```

### Step 2.运行黑盒内的应用
```java
   BlackBoxCore.get().launchApk("com.tencent.mm", userId);
```

### 多开应用操作
<img src="assets/multiw.gif" width="50%">

### 虚拟定位
虚拟定位基本原理：禁用基站和Wifi定位，修改GPS定位参数。

网络定位是根据访问者的IP定位位置的，黑盒作为客户端无法禁用网络定位。

可以通过[https://www.chaipip.com/](https://www.chaipip.com/)网址查看自己当前所在IP的定位

如果应用高度依赖网络IP定位，以下几种方法可以绕过:

1. 使用vpn走流量，伪装IP。
2. 禁用手机Wifi，改用移动网络上网。Wifi环境下的IP地址定位较准，移动网络IP只能定位到城市大小的范围。

### 设备伪装
目前设备伪装是硬编码，暂不提供用户界面操作

* 小米手机上测试改机操作成功
* 华为设备测试失败，需要增强操作


### 相关API
#### 获取黑盒内已安装的应用
```java
   // flgas与常规获取已安装应用保持一致即可
   BlackBoxCore.get().getInstalledApplications(flags, userId);
   
   BlackBoxCore.get().getInstalledPackages(flags, userId);
```

#### 获取黑盒内的User信息
```java
   List<BUserInfo> users = BlackBoxCore.get().getUsers();
```
更多其他操作看BlackBoxCore函数名大概就知道了。


#### Xposed相关
- 已支持使用XP模块
- Xposed已粗略过检测，[Xposed Checker](https://www.coolapk.com/apk/190247)、[XposedDetector](https://github.com/vvb2060/XposedDetector) 均无法检测


## 如何参与开发？
### 应用分2个模块
- app模块，用户操作与UI模块
- Bcore模块，此模块为BlackBox的核心模块，负责完成整个黑盒的调度。

如需要参与开发请直接pr就可以了，相关教程请Google或者看 [如何在 GitHub 提交第一个 pull request](https://chinese.freecodecamp.org/news/how-to-make-your-first-pull-request-on-github/)
### PR须知
1. 中英文说明都可以，但是一定要详细说明问题
2. 请遵从原项目的代码风格、设计模式，请勿个性化。
3. PR不分大小，有问题随时欢迎提交。

### 已知问题
* 死亡进程重启出现多进程问题，活动无法正常交互。临时解决方法：手动将目标进程都杀死，重启目标应用
* ~~vlc应用启动崩溃，96bd784版本正常启动~~
* 火狐浏览器点击输入网址崩溃
* 调用content provider的getType崩溃
* 静态广播测试失败
* 多进程缺陷，初步怀疑新进程启动后，Activity中的原活动对应的pid未更新，导致出现新活动对应多进程。卡顿是由于初始化新进程导致的

## 赞助
本项目为免费开源项目，日常维护耗费大量精力。如想加快进度或请作者喝杯咖啡。

- BTC: 1HhctWiQ6dBQomKPJjty3P1wL9CuK9eCqM
- USDT（ERC20）: 0x408336bcaef0c5eb11772024a06079ab2e14e6cd

## 感谢
- [VirtualApp](https://github.com/asLody/VirtualApp)
- [VirtualAPK](https://github.com/didi/VirtualAPK)
- [BlackReflection](https://github.com/CodingGay/BlackReflection)
- [AndroidHiddenApiBypass](https://github.com/LSPosed/AndroidHiddenApiBypass)
- [Pine](https://github.com/canyie/pine)

### License

> ```
> Copyright 2022 BlackBox
>
> Licensed under the Apache License, Version 2.0 (the "License");
> you may not use this file except in compliance with the License.
> You may obtain a copy of the License at
>
>    http://www.apache.org/licenses/LICENSE-2.0
>
> Unless required by applicable law or agreed to in writing, software
> distributed under the License is distributed on an "AS IS" BASIS,
> WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
> See the License for the specific language governing permissions and
> limitations under the License.
> ```
