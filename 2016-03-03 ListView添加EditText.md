#ListView 添加 editText
    因为ListView有view重用机制，因此edittext中数据不能直接拿取。
      
      解决方法是使用TextWatcher监听editText，使用一个数据容器存储相应的输入
