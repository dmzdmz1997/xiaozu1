/** 定义控制器层 */
app.controller('indexController', function ($scope, $controller, baseService) {

    /** 指定继承baseController */
    $controller('baseController', {$scope: $scope});
    /** 定义获取登录用户名方法 */
    $scope.showName = function () {
        baseService.sendGet("/user/showName")
            .then(function (response) {
                $scope.loginName = response.data.loginName;

            });
    };

    $scope.homeInsert = function () {
        alert(JSON.stringify($scope.user));
    }

    /**根据登录的用户查找用户所有订单*/
    $scope.searchParam = {page:1,rows:2}
    $scope.findOrdersByUserId = function () {
        baseService.sendPost("/user/findOrdersByUserId", $scope.searchParam)
            .then(function (response) {
                if (response) {
                    $scope.orders = response.data[1].orders;
                    $scope.totalPages = response.data[0].totalPages;
                    /** 调用初始化页码方法 */
                    initPageNum();

                }
            });
    }
    /** 提取json某个属性，返回拼接的字符串(逗号分隔) */
    $scope.jsonArr2Str = function (jsonStr) {
        var json = JSON.parse(jsonStr);
        var str = '';
        for (var i in json) {
            str += json[i] + ' ';
        }
        return str;
    }

    /** 根据分页搜索方法 */
    $scope.pageSearch = function (page) {
        page = parseInt(page);
        /** 页码验证 */
        if (page >= 1 && page <= $scope.totalPages
            && page != $scope.searchParam.page) {
            $scope.searchParam.page = page;
            $scope.findOrdersByUserId();
        }
    };
    /** 定义初始化页码方法 */
    var initPageNum = function(){
        /** 定义页码数组 */
        $scope.pageNums = [];
        /** 获取总页数 */
        var totalPages = $scope.totalPages;
        /** 开始页码 */
        var firstPage = 1;
        /** 结束页码 */
        var lastPage = totalPages;
        /** 前面有点 */
        $scope.firstDot = true;
        /** 后面有点 */
        $scope.lastDot = true;

        /** 如果总页数大于5，显示部分页码 */
        if (totalPages > 5){
            // 如果当前页码处于前面位置
            if ($scope.searchParam.page <= 3){
                lastPage = 5; // 生成前5页页码
                $scope.firstDot = false; // 前面没点
            }
            // 如果当前页码处于后面位置
            else if ($scope.searchParam.page >= totalPages - 3){
                firstPage = totalPages - 4;  // 生成后5页页码
                $scope.lastDot = false; // 后面没点
            }else{ // 当前页码处于中间位置
                firstPage = $scope.searchParam.page - 2;
                lastPage = $scope.searchParam.page + 2;
            }
        }else{
            $scope.firstDot = false; // 前面没点
            $scope.lastDot = false; // 后面没点

        }
        /** 循环产生页码 */
        for (var i = firstPage; i <= lastPage; i++){
            $scope.pageNums.push(i);
        }
    };
    $scope.orderStatus='';
    /**判断订单状态*/
    $scope.showStatus=function (order) {
        if (order.paymentTime!=null&&order.consign!=null){
            $scope.orderStatus='3';
        }else if (order.paymentTime!=null&&order.consign==null){
            $scope.orderStatus='2';
        }else{
            $scope.orderStatus='1';
        }
        switch (order.status){
            case '1':
                return "立即付款";
                break;
            case '2':
                return "提醒发货";
                break;
            case '3':
                return "确认收货";
                break;
        }
    }
});