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

        if(extractPrice=='' || extractPrice<=0){
            $scope.extractCode = 0;
            $scope.extractMsg = "余额不足!";
            return;
        }


        $http({
            method: 'GET',
            params:{
                extractPhone:extractPhone,
                extractPrice:extractPrice
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
