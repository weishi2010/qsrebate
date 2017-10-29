adminModule.controller("productController", ["$scope", "$http", function ($scope, $http) {
    $scope.search_systemName='';

    $scope.query = function () {
        productName= $("#search_productName").val();
        productId =$("#search_productId").val();
        couponType =$("#search_couponType").val();
        $http.get("/admin/getProducts.json",{params:{productName: encodeURIComponent(productName),productId:productId,couponType:couponType}}).success(function (response) {
            if (response.success) {
                $scope.products = response.products;
            }
        });
    }

    $scope.selectAll = function(event){
        var action = event.target;
        var groupCheckbox=$("input[name='ckb']");
        if(action.checked){
            //全部选中
            for(i=0;i<groupCheckbox.length;i++){
                groupCheckbox[i].checked = true;
            }
        }else{
            //取消选中
            for(i=0;i<groupCheckbox.length;i++){
                groupCheckbox[i].checked = false;
            }
        }
    }


    $scope.batchResetTop = function () {
        if(!confirm("确定要批量置顶吗？")){
            return;
        }

        var groupCheckbox=$("input[name='ckb']");
        array = [];
        for(i=0;i<groupCheckbox.length;i++){
            if(groupCheckbox[i].checked){
                var val =groupCheckbox[i].value;
                array.push(val);
            }
        }
        productIds = array.join(",");

        $http.get("/admin/batchResetTop.json",{params:{productIds:productIds}}).success(function (response) {
            if (response.success) {
                $scope.query();
            }
        });

    }

    $scope.batchResetCancel = function () {
        if(!confirm("确定要批量置顶吗？")){
            return;
        }

        var groupCheckbox=$("input[name='ckb']");
        array = [];
        for(i=0;i<groupCheckbox.length;i++){
            if(groupCheckbox[i].checked){
                var val =groupCheckbox[i].value;
                array.push(val);
            }
        }
        productIds = array.join(",");

        $http.get("/admin/batchResetCancel.json",{params:{productIds:productIds}}).success(function (response) {
            if (response.success) {
                $scope.query();
            }
        });

    }

    $scope.save = function () {
        systemName= $("#systemName").val();
        accountType= $("#accountType").val();
            $http.get('/system/add',{params:{'name':systemName,'accountType':accountType}}).success(function(response){
            var result = response.data;
            if (result.success) {
                $("#addSystemModal").modal("hide");
            }else{
                alert("系统异常，请稍后重试!")
            }

        });
    }

    $scope.edit = function (productId) {
        $("#editBtn"+productId).attr("data-toggle", "modal");
        $("#editBtn"+productId).attr("data-target", "#editProductModal");

        $http.get("/admin/findProduct.json",{params:{productId:productId}}).success(function (response) {
            if (response.success) {
                $scope.product = response.product;
            }
        });
    }

    $scope.update = function () {
        $http.get("/system/update").then(function (response) {
            var result = response.data;
            if (result.success) {

            }
        });
    }


    $scope.query();
}]);

function editProduct(obj) {
    $(obj).attr("data-toggle", "modal");
    $(obj).attr("data-target", "#editProductModal");
}
