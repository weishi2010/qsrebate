adminModule.controller("productController", ["$scope", "$http", function ($scope, $http) {
    $scope.search_systemName='';
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 0,
        itemsPerPage: 15,
        pagesLength: 5,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function(){
            $scope.query();
        }
    };

    $scope.query = function () {
        productName= $("#search_productName").val();
        productId =$("#search_productId").val();
        couponType =$("#search_couponType").val();
        thirdCategory =$("#thirdCategory").val();
        status =$("#search_status").val();

        page =  $scope.paginationConf.currentPage;
        pageSize =  $scope.paginationConf.itemsPerPage;

        $http.get("/admin/getProducts.json",{params:{productName: encodeURIComponent(productName),productId:productId,status:status,couponType:couponType,thirdCategory:thirdCategory,page:page,pageSize:pageSize,r:Math.random()}}).success(function (response) {
            if (response.success) {
                $scope.products = response.products;
                $scope.paginationConf.totalItems = response.totalItem;

            }
        });
    }

    $scope.getFirstCategoryList = function () {

        $http.get("/admin/getFirstCategory.json",{params:{r:Math.random()}}).success(function (response) {
            if (response.success) {
                $scope.firstCategoryList = response.firstCategoryList;

            }
        });
    }

    $scope.getSecondCategoryList = function () {
        firstCategoryId= $("#firstCategory").val();

        $http.get("/admin/getSecondCategory.json",{params:{categoryId: firstCategoryId,r:Math.random()}}).success(function (response) {
            if (response.success) {
                $scope.secondCategoryList = response.secondCategoryList;

            }
        });
    }

    $scope.getThirdCategoryList = function () {
        secondCategoryId= $("#secondCategory").val();

        $http.get("/admin/getThirdCategory.json",{params:{categoryId: secondCategoryId,r:Math.random()}}).success(function (response) {
            if (response.success) {
                $scope.thirdCategoryList = response.thirdCategoryList;

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

    $scope.getFirstCategoryList();
}]);

function editProduct(obj) {
    $(obj).attr("data-toggle", "modal");
    $(obj).attr("data-target", "#editProductModal");
}
