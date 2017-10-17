var app = angular.module('myApp', []);
app.controller('extractCtrl', function ($scope,$http) {

    $scope.submitExtract  = function (extractPrice) {
        $scope.extractCode = 1;
        $scope.extractMsg = "";
        extractPhone= $scope.extractPhone;
        if(extractPhone==''){
            $scope.extractCode = 0;
            $scope.extractMsg = "请输入手机号码!";
            return;
        }

        if(extractPrice=='' || extractPrice<20){
            $scope.extractCode = 0;
            $scope.extractMsg = "很抱歉，账户余额大于20元才能进行提现!";
            return;
        }


        $http({
            method: 'GET',
            params:{
                extractPhone:extractPhone,
                extractPrice:extractPrice,
                rd:Math.random()
            },
            url: '/personal/extractPrice.json?callback=1'
        }).then(function successCallback(response) {
            resultObj = response.data;
            if(resultObj.code==1){
                location.href ='/personal/extractDetail'
            }else{
                $scope.extractCode = resultObj.code;
                $scope.extractMsg = resultObj.msg;
            }
        }, function errorCallback(response) {
            // 请求失败执行代码
        });
    }
});
