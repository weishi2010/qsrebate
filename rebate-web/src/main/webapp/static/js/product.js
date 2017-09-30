var app = angular.module('myApp', []);
app.controller('productCtrl', function ($scope,$http) {

    $scope.products = [];
    $scope.searchParam ='';
    $scope.search  = function () {

        $http({
            method: 'GET',
            params:{
            },
            url: '/product/searchProducts.json?page=1&params='+$scope.searchParam+ '&r=' + Math.random()
        }).then(function successCallback(response) {

            if(response.data){
                var list = response.data.products;
                var sum = list.length;
                if(sum>0){
                    for (var i = 0; i < sum; i++) {
                        $scope.products.push(response.data.products[i])
                    }
                }
            }
        }, function errorCallback(response) {
            // 请求失败执行代码
        });
    }


    $scope.redirectJdPromotionUrl = function redirectJdPromotionUrl(skuId) {
        $.ajax({
            type: 'GET',
            url: '/product/jdShortUrl.json?skuId=' + skuId + "&r=" + Math.random(),
            dataType: 'json',
            success: function (reponse) {
                var url = reponse.url;
                location.href = url;
            },
            error: function (xhr, type) {
                console.log('加载更多数据错误！');
            }
        });
    }

    $scope.hasMore = false;
    $scope.SName = "已经没有数据了";
    $scope.do_refresher = function() {
        page = 1;
        secondCategoryList = ''
        tab = 1;
        $http({
            method: 'GET',
            params:{
            },
            url: '/product/products.json?page=' + page + "&secondCategoryList=" + secondCategoryList + "&tab=" + tab + "&r=" + Math.random(),
        }).then(function successCallback(response) {

            if(response.data){
                var list = response.data.products;
                var sum = list.length;
                var result = '';
                if(sum>0){
                    $scope.hasMore = true;
                    for (var i = 0; i < sum; i++) {
                        $scope.products.push(response.data.products[i])
                    }
                }else{
                    $scope.hasMore = false;
                }
            }
        }, function errorCallback(response) {
            // 请求失败执行代码
        });
    }
});
