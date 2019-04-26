app.controller('infoController',function ($scope,$controller,baseService) {
    //继承baseController
    $controller('indexController',{$scope:$scope});

    // 定义数据格式
    $scope.user = {nickName:"",sex:"",birthday:"",address:{province:"",city:"",area:""},headPic:''};

    //回显个人信息
    $scope.findInfo = function () {
        baseService.sendGet("user/findInfo")
            .then(function (response) {
                $scope.user = response.data;
                $scope.user.address = JSON.parse($scope.user.address);
            });
    };
    //查询所有省
    $scope.findProvince = function () {
        baseService.sendGet("/user/findProvince")
            .then(function (response) {
               $scope.provinces= response.data;
            });
    };

    // $scope.$watch: 它可以监控$scope中的变量发生改变，就会调用一个函数
    // $scope.$watch: 监控一级分类id,发生改变，查询二级分类
    $scope.$watch('user.address.province', function (newVal, oldVal) {
        //alert("新值：" + newVal + ",旧值:" + oldVal);
        if (newVal){ // 不是undefined、null
            // 查询二级分类
            baseService.sendGet("user/findCityByProvinceId?provinceId=" + newVal)
                .then(function (response) {
                    $scope.cities = response.data;
                });
        }else {
            $scope.cities = [];
        }
    });

    $scope.$watch('user.address.city', function (newVal, oldVal) {
        //alert("新值：" + newVal + ",旧值:" + oldVal);
        if (newVal){ // 不是undefined、null
            // 查询二级分类
            baseService.sendGet("user/findAreaByCityId?cityId=" + newVal)
                .then(function (response) {
                    $scope.areas = response.data;
                });
        }else {
            $scope.areas = [];
        }
    });
    //保存或者更新用户信息
    $scope.saveOrUpdate = function () {
            baseService.sendPost("/user/saveUserInfo", $scope.user)
                .then(function (response) {
                    if (response.data) {
                        alert("保存成功");
                    }else {
                        alert("保存失败");
                    }
                });
    }

    // 定义文件上传
    $scope.uploadFile = function () {
        // 调用服务层上传文件
        baseService.uploadFile().then(function(response){
            // 获取响应数据 {status : 200, url : 'http://192.168.12.131/group1/xxx/xx/x.jpg'}
            if(response.data.status == 200){
                // 获取图片url $scope.picEntity : {url : '', color:''}
                $scope.user.headPic= response.data.url;
            }else{
                alert("上传失败！");
            }
        });
    };
});