// 定义购物车的控制器
app.controller('orderController', function ($scope, $controller, $interval, $location, baseService) {

    // 继承indexController
    $controller('indexController', {$scope: $scope});

    // 生成微信支付二维码
    $scope.genPayCode = function (orderId) {
        baseService.sendGet("/user/genPayCode?orderId="+orderId).then(function (response) {
            // 获取响应数据  response.data : {outTradeNo: '', money : 1, codeUrl : ''}
            // 获取交易订单号
            $scope.outTradeNo = response.data.outTradeNo;
            // 获取支付金额
            $scope.money = (response.data.totalFee / 100).toFixed(2);
            // 获取支付URL
            $scope.codeUrl = response.data.codeUrl;

            // 支付二维码
            document.getElementById("qrcode").src = "/barcode?url=" + $scope.codeUrl;

            // 开启定时器
            // 第一个参数：定时需要回调的函数
            // 第二个参数：间隔的时间毫秒数 3秒
            // 第三个参数：总调用次数 100
            var timer = $interval(function(){

                // 发送异步请求，获取支付状态
                baseService.sendGet("/user/queryPayStatus?outTradeNo="
                    + $scope.outTradeNo).then(function(response){

                    // 获取响应数据: response.data: {status : 1|2|3} 1:支付成功 2：未支付 3:支付失败
                    if (response.data.status == 1){// 支付成功
                        // 取消定时器
                        $interval.cancel(timer);
                        // 跳转到支付成功的页面
                        location.href = "/order/paysuccess.html?money=" + $scope.money;
                    }
                    if (response.data.status == 3){
                        // 取消定时器
                        $interval.cancel(timer);
                        // 跳转到支付失败的页面
                        location.href = "/order/payfail.html";
                    }
                });
            }, 3000, 100);

            // 总调用次数结束后，需要调用的函数
            timer.then(function(){
                // 关闭订单
                $scope.tip = "二维码已过期，刷新页面重新获取二维码。";
            });

        });
    };


    // 获取支付的总金额
    $scope.getMoney = function () {
        return $location.search().money;
    };
    /**根据订单状态跳转到相应处理页面*/
    $scope.pay = function (order) {
        switch (order.status){
            case '1':
                location.href = "/order/pay.html?outTradeNo="+order.outTradeNo;
                break;
            case '2':
                break;
            case '4':
                break;
        }

    }

    // 获取支付的总金额
    $scope.getOrderId = function () {
        return $location.search().outTradeNo;
    };
});