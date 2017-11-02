var adminModule = angular.module("admin", ["ngRoute","tm.pagination"]).config(function ($routeProvider) {
    $routeProvider.when("/product/list", {
        templateUrl : "/static/page/productList.html",
        controller : "productController"
    }).otherwise({
        templateUrl : "/static/page/productList.html",
        controller : "productController"
    })
});
