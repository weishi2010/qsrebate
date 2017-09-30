var app = angular.module('myApp', []);
app.controller('activityCtrl', function ($scope,$http) {

    $scope.activityList = [];
    $scope.loadData  = function () {

        $http({
            method: 'GET',
            params:{
                r:+Math.random()
            },
            url: '/activity/activityList.json'
        }).then(function successCallback(response) {
            if(response.data){
                $scope.activityList = response.data.activityList
            }
        }, function errorCallback(response) {
            // 请求失败执行代码
        });
    }

    $scope.loadData();
});
