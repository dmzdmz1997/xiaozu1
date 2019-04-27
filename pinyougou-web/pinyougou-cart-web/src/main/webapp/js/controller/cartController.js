// 定义购物车的控制器
app.controller('cartController', function ($scope, $controller, baseService) {

    // 继承baseController
    $controller('baseController', {$scope: $scope});

    // 查询购物车
    $scope.findCart = function () {
        baseService.sendGet("/cart/findCart").then(function (response) {
            // 获取响应数据
            $scope.carts = response.data;

            // 定义json对象封装统计的数据
            $scope.totalEntity = {totalNum: 0, totalMoney: 0};
            // 迭代用户的购物车集合
            /*for (var i = 0; i < $scope.carts.length; i++) {
                // 获取商家的购物车
                var cart = $scope.carts[i];
                // 迭代商家购物车中的商品
                for (var j = 0; j < cart.orderItems.length; j++) {
                    // 获取购买的商品
                    var orderItem = cart.orderItems[j];

                    // 统计购买数量
                    $scope.totalEntity.totalNum += orderItem.num;
                    // 统计总金额
                    $scope.totalEntity.totalMoney += orderItem.totalFee;

                }
            }*/
        });
    };

    // 添加商品到购物车
    $scope.addCart = function (itemId, num) {
        baseService.sendGet("/cart/addCart?itemId="
            + itemId + "&num=" + num).then(function (response) {
            // 获取响应数据
            if (response.data) {
                // 重新查询购物车
                $scope.findCart();
            }
        });
    };


    //单件商品选择
    $scope.orderItemSelect = function (orderItem, orderItemsOfCart, cart, carts) {
        //flag是一个标志，如果其中一条商家数据为false的话，那flag就为false，当flag为true的时候才有资格判断商家是不是全选，当商家也全选时，才有资格给全选勾选
        var flag = true;
        //如果点击的商品为true,才判断商家下别的checked是不是等于false,如果为false则，全选 商家全选都为false
        if (orderItem.checked) {
            //添加商品总数
            $scope.totalEntity.totalNum += orderItem.num;
            //增加商品总价
            $scope.totalEntity.totalMoney += orderItem.totalFee;
            //判断同商家下的其他单个商品的checked属性是否为false
            angular.forEach(orderItemsOfCart, function (innerItem) {
                /*如果商家下的某条商品信息为没被选中或者没被定义（假设一个商家下有多条商品，点击商品外的商品由于还未点击所以还没有被定义）
                * 那么flag即为false。*/
                if (innerItem.checked == false || typeof(innerItem.checked) == "undefined") {
                    flag = false;
                }
            });
        } else {
            //减少商品总数
            $scope.totalEntity.totalNum -= orderItem.num;
            //减少商品总价
            $scope.totalEntity.totalMoney -= orderItem.totalFee;
            //如果点击的商品为false，则allcheck全选的状态为false
            $scope.allcheck = false;
            //商家选择状态属性也为false
            cart.checked = false;
            flag = false;
        }
        if (flag) {
            //单个商品点击时，判断完flag就会令传入的商家状态变为全选
            cart.checked = true;
            angular.forEach(carts, function (outerItem) {
                //如果某个商家没被选中或者还没被定义,flag就为false（交由$scope）
                if (outerItem.checked == false || typeof(outerItem.checked) == "undefined") {
                    flag = false;
                }
            });
        }
        if (flag) {
            $scope.allCheck = true;
        }
        $scope.addSelect(carts);
    };

    //商家选择
    $scope.cartItemSelect = function (cart, carts) {
        if (cart.checked) {
            var flag = true;
            angular.forEach(cart.orderItems, function (innerItem) {
                //添加商品总数
                $scope.totalEntity.totalNum += innerItem.num;
                //增加商品总价
                $scope.totalEntity.totalMoney += innerItem.totalFee;
                innerItem.checked = true;
            });
            angular.forEach(carts, function (outerItem) {
                if (outerItem.checked == false || typeof(outerItem.checked) == "undefined") {
                    flag = false;
                }
            });
            if (flag) {
                $scope.allCheck = true;
            }
        }
        else {
            $scope.allCheck = false;
            angular.forEach(cart.orderItems, function (innerItem) {
                //减少商品总数
                $scope.totalEntity.totalNum -= innerItem.num;
                //减少商品总价
                $scope.totalEntity.totalMoney -= innerItem.totalFee;
                innerItem.checked = false;
            });
        }
        $scope.addSelect(carts);
    }
    ;/ *全部商铺商品选择* /
    $scope.allSelect = function (carts) {
        if ($scope.allCheck) {
            angular.forEach($scope.carts, function (cart) {
                cart.checked = true;
                angular.forEach(cart.orderItems, function (innerItem) {
                    //增加商品总数
                    $scope.totalEntity.totalNum += innerItem.num;
                    //增加商品总价
                    $scope.totalEntity.totalMoney += innerItem.totalFee;
                    innerItem.checked = true;
                })
            });
        }
        else {
            angular.forEach($scope.carts, function (cart) {
                cart.checked = false;
                angular.forEach(cart.orderItems, function (innerItem) {
                    //减少商品总数
                    $scope.totalEntity.totalNum -= innerItem.num;
                    //减少商品总价
                    $scope.totalEntity.totalMoney -= innerItem.totalFee;
                    innerItem.checked = false;
                })
            });
        }
        $scope.addSelect(carts);
    };
});