adminModule.controller("productController", ["$scope", "$http", function ($scope, $http) {
    $scope.search_systemName='';

    $scope.query = function () {
        productName= $("#search_productName").val();
        productId =$("#search_productId").val();
        couponType =$("#search_couponType").val();
        $http.get("/admin/getProducts.json",{params:{productName: encodeURIComponent(productName),productId:productId,couponType:couponType,r:Math.random()}}).success(function (response) {
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

        $http.get("/admin/batchResetTop.json",{params:{productIds:productIds,r:Math.random()}}).success(function (response) {
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

        $http.get("/admin/batchResetCancel.json",{params:{productIds:productIds,r:Math.random()}}).success(function (response) {
            if (response.success) {
                $scope.query();
            }
        });

    }

    $scope.edit = function (productId) {
        $("#editBtn"+productId).attr("data-toggle", "modal");
        $("#editBtn"+productId).attr("data-target", "#editProductModal");
        $scope.product = [];
        $scope.sortWeight = 0;
        $http.get("/admin/findProduct.json",{params:{productId:productId,r:Math.random()}}).success(function (response) {
            if (response.success) {
                $scope.product = response.product;
                $scope.sortWeight =$scope.product.sortWeight;
            }
        });
    }

    $scope.update = function (productId) {
        sortWeight = $("#sortWeight").val();
        if(!sortWeight){
            alert("请输入完整参数!")
            return;
        }

        $http.get("/admin/updateProduct.json",{params:{productId:productId,sortWeight:sortWeight,r:Math.random()}}).success(function (response) {
            if (response.success) {
                $scope.query();
                $("#editProductModal").modal("hide");
            }
        });
    }


    $scope.query();
}]);

function editProduct(obj) {
    $(obj).attr("data-toggle", "modal");
    $(obj).attr("data-target", "#editProductModal");
}
