var adminModule = angular.module("admin", ["ngRoute","tm.pagination"]).config(function ($routeProvider) {
    $routeProvider.when("/product/list", {
        templateUrl : "/static/page/admin/productList.html",
        controller : "productController"
    }).when("/extract/list", {
        templateUrl : "/static/page/admin/extractList.html",
        controller : "extractController"
    }).when("/order/list", {
        templateUrl : "/static/page/admin/orderList.html",
        controller : "orderController"
    }).otherwise({
        templateUrl : "/static/page/admin/userList.html",
        controller : "userController"
    })
});
