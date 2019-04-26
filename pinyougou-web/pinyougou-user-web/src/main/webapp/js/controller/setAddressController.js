/** 定义控制器层 */
app.controller('setAddressController', function ($scope, baseService) {

    $scope.findCitiesByProvinceId = function (parentId) {
        baseService.sendGet("/address/findCitiesByProvinceId" + "?parentId=" + parentId)
            .then(function (response) {
                $scope.citiesList = response.data.citiesList;
            });
    };

    $scope.$watch('address.provinceId', function (newVal) {
        if (newVal) {    //不是undefined,null
            $scope.findCitiesByProvinceId(newVal);
        } else {
            $scope.citiesList = [];
        }
    });
    $scope.findAreasByCityId = function (parentId) {
        baseService.sendGet("/address/findAreasByCityId" + "?parentId=" + parentId)
            .then(function (response) {
                $scope.areasList = response.data.areasList;
            });
    };
    $scope.$watch('address.cityId', function (newVal) {
        if (newVal) {    //不是undefined,null
            $scope.findAreasByCityId(newVal);
        } else {
            $scope.citiesList = [];
        }
    });

    $scope.findAllAddress = function () {
        baseService.sendGet("/address/findAllAddress")
            .then(function (response) {
                $scope.provincesList = response.data.provincesList;
            });
    };


    $scope.showAddress = function () {
        /** 获取登录用户名 */
        baseService.sendGet("/address/showUserName").then(function (response) {
            $scope.loginName = response.data.loginName;
        });
        /** 获取用户地址 */
        baseService.sendGet("/address/showUserAddress")
            .then(function (response) {

                $scope.addressList = response.data.addressList;
            });


    };

    $scope.address = {contact:"",address:"",mobile:"",alias:""};
    $scope.addAddress = function () {
        alert(JSON.stringify($scope.address));
        baseService.sendPost("/address/addAddress", $scope.address).then(function (response) {

        });
    };

    $scope.getAlias = function (name) {
        $scope.address.alias = name
    };
    $scope.updateaddress={};
    $scope.getUserAddress = function (index) {
        $scope.updateaddress = $scope.addressList[index];
        alert(JSON.stringify($scope.updateaddress) )

    }

});