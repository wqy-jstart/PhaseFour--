# 难点剖析

### 1.在添加管理员时，需指定该管理员的角色，并在添加时，将一个管理员可能对应的多个角色，插入到管理员表和管理员角色表中。

- #### 在添加管理员中的下拉框内，获取除ID为1（系统管理员）之外的其他角色

  - 在`RoleServiceImpl`中实现该方法：

    ```java
    /**
     * 实现Service接口中获取角色列表的功能(实现过程中移除Id为1的角色----系统管理员)
     * @return 返回List集合
     */
    @Override
    public List<RoleListItemVO> list() {
        log.debug("开始处理查询角色列表的业务!");
        List<RoleListItemVO> list = roleMapper.list();// 获取List列表集合
        Iterator<RoleListItemVO> iterator = list.iterator();// 创建一个迭代器
        while (iterator.hasNext()){ // 判断是否存在下一个元素
            RoleListItemVO item = iterator.next(); // 获取下一个对象元素
            if (item.getId()==1){ // 若该对象的id为1
                iterator.remove(); // 从集合中使用迭代器移除该对象
                break;// 跳出分支
            }
        }
        return list;
    }
    ```

  - 在`RoleController`控制器中调用该实现类,并返回`JsonResult`结果:

    ```java
    /**
     * 执行获取角色列表的请求
     * 用于前端的角色下拉框
     * @return 返回JsonResult
     */
    @ApiOperation("查询角色列表")
    @ApiOperationSupport(order = 100)
    @GetMapping("")
    public JsonResult<List<RoleListItemVO>> list(){
        log.debug("开始处理[查询角色列表]的请求");
        List<RoleListItemVO> list = iRoleService.list();
        return JsonResult.ok(list);
    }
    ```

  - 前端通过`url`请求路径发出`axios`异步请求来获取角色的信息并绑定到下拉框内遍历`roleListOptions`获取`name`和`id`

  - 添加过程中,通过选择角色对应的id与对象`roleIds: []`进行绑定,点击添加按钮后与`ruleForm`一同绑定,传到`AdminAddNewDTO`中

    ```javascript
    loadRoleList() {
      console.log('loadRoleList');
      let url = "http://localhost:9081/roles" // 请求路径
      console.log('url=' + url);
      this.axios.get(url).then((response) => {// 发送异步请求
        let responseBody = response.data;
        this.roleListOptions = responseBody.data;//将获取响应的数据中的data数据赋值给tableData
      })
    },
    ```

- 在添加管理员并选择对应的多个角色后.后端`AdminServiceImpl`接收到传递的信息,并将除`roleIds:[]`之外的信息插入到`Admin`表中后,在利用`Long[]`接收传递的`roleIds:[]`,并创建`AdminRole`的引用类型数组对象,长度为传递的角色id的长度,在遍历`Long[]`的同时,创建`AdminRole`对象,将管理员id与遍历的角色id(roleIds[i])设置到`AdminRole`引用数组中,最后将数组传递到`AdminRoleMapper`中的`insertBatch`方法中进行批量插入管理员角色表,并抛出相关异常.

  ```java
  /*
   执行插入管理员与角色关联的数据
   条件:
   (1).上次插入管理员后获取的roleIds=[?,?,?]
   (2).选择的角色对应的id数组
   */
  Long[] roleIds = adminAddNewDTO.getRoleIds();// 获取客户端选择角色传入的多个角色roleIds
  AdminRole[] adminRoles = new AdminRole[roleIds.length];// 创建一个角色管理员的引用数组,长度为用户选择的数量
  LocalDateTime now = LocalDateTime.now();// 获取当前的时间
  for (int i = 0; i < roleIds.length; i++) { // 遍历等长的数组向角色管理员关联表中插入数据(一个管理员对应多个角色)
      AdminRole adminRole = new AdminRole();// 创建角色管理员对象
      adminRole.setAdminId(admin.getId());// 设置插入后的管理员id
      adminRole.setRoleId(roleIds[i]);// 设置该管理员id下选择的所有角色id
      adminRole.setGmtCreate(now); // 设置当前时间
      adminRole.setGmtModified(now);
      adminRoles[i] = adminRole;// 将封装好的角色管理员对象放到AdminRole[]数组中
  }
  rows = adminRoleMapper.insertBatch(adminRoles);// 调用批量插入关联表的方法,传入要插入的管理员角色对象
  if (rows != roleIds.length){ // 判断如果影响的行数与插入的结果不一致时,抛出异常
      String message = "添加管理员失败，服务器忙，请稍后再次尝试！";
      log.debug(message);
      throw new ServiceException(ServiceCode.ERR_INSERT,message);
  }
  ```

### 2.实现启用和禁用

- 在`Element UI`框架中,使用启用禁用的控件,通过`<template slot-scope="scope">`标签来根据获取的管理员enable字段值1或0来进行启用和禁用

  - 该控件启用默认为1,禁用默认为0

    ```javascript
    <template slot-scope="scope">
      <!-- 1开 0关 -->
      <el-switch
          @change="changeEnable(scope.row)"
          v-model="scope.row.enable"
          :disabled="scope.row.id == 1"//当管理员id为1(系统管理员)时,不显示该控件
          :active-value="1"
          :inactive-value="0"
          active-color="#13ce66"
          inactive-color="#aaaaaa">
      </el-switch>
    </template>
    ```

  - #### 方法中实现:

  ```javascript
  changeEnable(admin) {
    console.log('admin id=' + admin.id);
    //点击后获取的enable值
    console.log('admin enable=' + admin.enable);
    let enableText = ['禁用', '启用'];
    let url = 'http://localhost:9081/admins/' + admin.id;
    if (admin.enable == 1) { // 如果点击后enable为1,说明是启用操作,则请求路径应为处理启用的路径
      console.log("启用管理员")
      url += '/enable';
    } else {
      console.log("禁用管理员")
      url += '/disable';
    }
    console.log('url=' + url)
    this.axios.post(url).then((response) => {
      let responseBody = response.data;
      if (responseBody.state == 20000) {
        let message = '将管理员[' + admin.username + ']的启用状态改为[' + enableText[admin.enable] + ']成功!';
        this.$message({
          message: message,
          type: 'success'
        });
      } else { // 否则输出错误信息
        this.$message.error(responseBody.message);
      }
      if (responseBody.state == 40400) { // 数据不存在的时候才刷新
        this.loadAlbumList();
      }
    })
  },
  ```

- 后端在`AdminServiceImpl`实现类中,实现接口中启用`enable`和禁用`disable`的两种方法,与此同时,创建一个私有的空返回值的修改启用和禁用的方法,来处理启用禁用两种状态的实现逻辑与细节

  ```java
  @Override
  public void setEnable(Long id) {
      updateEnableById(id, 1);// 调用该方法传入id,设置为启用状态
  }
  
  /**
   * 处理禁用管理员的业务
   *
   * @param id 禁用的管理员id
   */
  @Override
  public void setDisable(Long id) {
      updateEnableById(id, 0);// 调用该方法传入id,设置为禁用状态
  }
  
  /**
   * 该方法用来处理启用与禁用的逻辑
   *
   * @param id     id
   * @param enable 是否启用或禁用
   */
  private void updateEnableById(Long id, Integer enable) {
      String[] tips = {"禁用", "启用"};
      log.debug("开始处理【{}管理员】的业务，id参数：{}", tips[enable], id);
      // 判断id是否为1(系统管理员)
      if (id == 1) {
          String message = tips[enable] + "管理员失败，尝试访问的数据不存在！";
          log.debug(message);
          throw new ServiceException(ServiceCode.ERR_NOT_FOUND, message);
      }
      // 根据id查询管理员详情
      AdminStandardVO queryResult = adminMapper.getStandardById(id);
      if (queryResult == null) {
          String message = tips[enable] + "管理员失败,尝试访问的数据不存在！";
          log.debug(message);
          throw new ServiceException(ServiceCode.ERR_NOT_FOUND, message);
      }
      // 判断查询结果中的enable与方法参数enable是否相同
      if (enable.equals(queryResult.getEnable())) {
          String message = tips[enable] + "管理员失败，管理员账号已经处于" + tips[enable] + "状态！";
          log.debug(message);
          throw new ServiceException(ServiceCode.ERROR_CONFLICT, message);
      }
      // 创建admin对象,并封装id和enable这2个属性的值,并进行修改
      Admin admin = new Admin();
      admin.setId(id);
      admin.setEnable(enable);
      int rows = adminMapper.updateById(admin);
      if (rows != 1) {
          String message = tips[enable] + "管理员失败，服务器忙，请稍后再次尝试！";
          throw new ServiceException(ServiceCode.ERR_UPDATE, message);
      }
      log.debug("修改成功!");
  }
  ```


### 3.使用Spring Security框架完成用户的认证与授权

#### 



