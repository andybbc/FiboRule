# FIBO Rule
 斐波那契
 FIBO Rule - 实时AI智能决策引擎、规则引擎、风控引擎、数据流引擎。 
 通过可视化界面进行规则配置，无需繁琐开发，节约人力，提升效率，实时监控，减少错误率，随时调整； 支持规则集(复杂、脚本)、评分卡、决策树，名单库管理、机器学习模型、三方数据接入、定制化开发等；
## 概述

 FIBO Rule - 实时智能决策引擎

​	一款将公司的商业规则转化成商业决策，通过将公司的行业决策经验进行知识化，来辅助公司做各种商业决策的决策引擎。

 
## 开源交流
 加官方微信号，进开源交流群。
                             
                             
   ![|](https://www.fibo.cn/standard/image/git_weixin.jpg)
   
 扫描二维码，添加WhatsApp。
 
 
  ![|](https://www.fibo.cn/standard/image/whatsApp.jpg)
  
                                  
## FIBO Rule整体功能架构介绍

FIBO Rule整体功能架构如下图所示

![整体架构](https://www.fibo.cn/standard/image/arch.jpg)

包括几个中心和一个执行器，其中：

#### 1 数据中心

数据中心设计的目的是配置和获取各种类型的指标数据，而不用再编写大量的程序。支持多种类型的指标和数据获取方式，通过多种数据获取方式、业务人员或者技术人员可以轻松获取指标数据。
目前支持的指标获取如下：

##### 1) 基础指标

基础指标的数据获取有两种方式：一是在调用引擎时作为参数传入。二是在实现 [标准的指标中心](https://www.enginex.biz/docs/tech/datacenter.html) 的接口，在需要该指标时，会调用指标中心获取数据

##### 2) SQL指标

SQL指标是通过编写SQL获取指标数据，在需要该指标数据，会执行已经配置好的SQL，通过执行SQL，来动态获取数据。

##### 3) 接口指标

接口指标是通过配置Http的请求接口来获取指标数据，在需要该指标数据时，会调用该接口，用该接口返回的数据来获取指标数据。

##### 4) 衍生指标

衍生指标是用其他指标通过计算生成一个新的指标，支持表达式和`groovy`脚本等多种数据加工方式。

#### 2 策略中心

策略中心是用指标通过条件组合构成各种规则，支持多种策略编辑方式，不同的场景适合采用不同的策略。目前支持的策略编辑方式如下：

##### 1) 基础规则

平铺式的规则编辑方式，特别适合同一层的条件项很多，但没有复杂嵌套关系的场景。

##### 2) 复杂规则

复杂规则支持条件的无限嵌套和循环嵌套，支持过滤、求和等操作，适合有复杂嵌套关系的场景。

##### 3) 评分卡

评分卡支持多维，支持多种维度的条件组合。结果支持直接得分、系数得分、自定义等多种得分方式，适合各种业务的评分需求。

##### 4) 决策表

决策表支持普通的一维决策表和多维的交叉决策表。

##### 5) 名单库

名单库是判断某项信息是否在名单中的一个策略，支持自定义，根据业务需求配置各种名单库。

##### 6) 机器学习模型

机器学习模型支持通过其他机器学习平台训练出来的PMML文件，通过对PMML文件的配置管理，支持各种模型。

#### 3 引擎中心

引擎中心是对引擎的配置和管理，一个引擎是对外提供决策服务的一套决策流。研发人员可以在业务系统中调用引擎，获取引擎的决策结果后进行后续的业务处理。

##### 1) 引擎列表

对引擎的进行基本的管理和监控

##### 2) 决策流配置

对引擎的决策流进行配置，可以通过拖拉拽的方式进行决策流的配置，所见即所得。支持多种类型的决策节点，通过不同类型的决策的决策节点的组合构成一个完整的决策流。

#### 4 监控中心

监控中心是对整个决策流执行状态的监控、服务状态监控、报错通知，以及系统操作日志的监控

#### 5 分析中心

统计分析中心支持多个维度的统计分析报表

#### 6 系统管理

系统管理主要对系统权限相关的管理，包括：用户、角色、组织、资源


## 核心概念和基本组成关系介绍

FIBO Rule决策引擎系统核心概念有：指标、规则、决策节点、决策流、决策引擎。它们的组成关系如下： 

                       
  ![|](https://www.fibo.cn/standard/image/relation.png)
  
  
        
### 1 指标

指标是客户的某个维度的信息，可来源于公司内部数据或者第三方数据

### 2 规则

规则有指标+条件+动作组成，表示某种条件下做的动作

### 3 决策节点

决策流的核心组成部分，多种决策节点，支持不同业务场景的决策流设计

### 4 决策流

一个决策流，是一个决策流程，有多个决策节点串连组成

### 5 决策引擎

一个引擎，对外提供一套服务接口，使用方调用指定引擎来获取决策结果

### 6 部署参考文档

https://www.fibo.cn/docs/

### 7 demo演示

http://ex.fibo.cn




