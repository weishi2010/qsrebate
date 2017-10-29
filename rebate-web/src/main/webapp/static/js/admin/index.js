var adminModule = angular.module("admin", ["ngRoute"]).config(function ($routeProvider) {
    $routeProvider.when("/product/list", {
        templateUrl : "/static/page/productList.html",
        controller : "productController"
    }).otherwise({
        templateUrl : "/static/page/productList.html",
        controller : "productController"
    })
});
