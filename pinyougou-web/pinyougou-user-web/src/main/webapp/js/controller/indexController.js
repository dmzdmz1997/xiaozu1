/** 定义控制器层 */
app.controller('indexController', function($scope,$controller, baseService){

    /** 指定继承baseController */
    $controller('baseController',{$scope:$scope});
    /** 定义获取登录用户名方法 */
    $scope.showName = function(){
        baseService.sendGet("/user/showName")
            .then(function(response){
                $scope.loginName = response.data.loginName;

            });
    };
    $scope.homeInsert = function () {
        alert(JSON.stringify($scope.user));
    }
});