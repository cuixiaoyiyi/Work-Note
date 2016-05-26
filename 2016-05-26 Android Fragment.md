##问题
今天遇到一个问题 4 个RadioButton对应 4个Fragment，Fragment3里包含两个Fragment31 Fragment32，直接删除掉Fragment32后，Fragment1不能正常切换。只要点击过Fragment3之后，再次点击Fragment1，都是显示Fragment3.原因待查
debug时内存显示Fragment正常，但是页面就是不对。
##解决方案
用 Fragment31 直接替换Fragment3 ，
