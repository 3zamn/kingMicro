$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'oss/pdf/list',
        datatype: "json",
        colModel: [			
			{ label: 'id', name: 'id', width: 20, key: true ,hidden:true},
			{ label: '文件名', name: 'name', width: 50},
			{ label: '大小', name: 'size', width: 30},
            { label: 'URL地址', name: 'url', width: 220 ,formatter:function(value,row,index) {
				return '<a href=\''+value+'\' target=\'_blank\'>'+value+'</a>';
			}},
			 { label: '操作', name: 'type', width: 20 ,formatter:function(value,row,index) {
					return '<a href=\''+'../../pdf/web/viewer.html?file='+index.pdf+'\' >'+"预览"+'</a>';
					/*return '<a href=\''+'../../pdf/web/viewer.html'+'\' target=\'_parent\'>'+"预览"+'</a>';*/
				}},
			{ label: '创建者', name: 'creator', width: 20 },
			{ label: '创建时间', name: 'createDate', width: 50 }
        ],
		viewrecords: true,
        height: 385,
        rowNum: 10,
		rowList : [10,30,50],
        rownumbers: true, 
        rownumWidth: 25, 
        autowidth:true,
        multiselect: true,
        pager: "#jqGridPager",
        jsonReader : {
            root: "data.list",
            page: "data.currPage",
            total: "data.totalPage",
            records: "data.totalCount"
        },
        prmNames : {
            page:"page", 
            rows:"limit", 
            order: "order"
        },
        gridComplete:function(){
        	//隐藏grid底部滚动条
        	$("#jqGrid").closest(".ui-jqgrid-bdiv").css({ "overflow-x" : "hidden" }); 
        }
    });

    new AjaxUpload('#upload', {
        action: baseURL + 'oss/pdf/upload?token=' + token,
        name: 'file',
        autoSubmit:true,
        responseType:"json",
        onSubmit:function(file, extension){
            if(vm.config.type == null){
                alert("云存储配置未配置");
                return false;
            }
           /* if (!(extension && /^(jpg|jpeg|png|gif)$/.test(extension.toLowerCase()))){
                alert('只支持jpg、png、gif格式的图片！');
                return false;
            }*/
        },
        onComplete : function(file, r){
        	 if(r.code == 200){
                 alert('上传成功', function(){
                     vm.reload();
                 });
             }else{
                 alert(r.msg);
             }
        }
    });

});

var vm = new Vue({
	el:'#rrapp',
	data:{
		showList: true,
		title: null,
        config: {}
	},
    created: function(){
        this.getConfig();
    },
	methods: {
		query: function () {
			vm.reload();
		},
		getConfig: function () {
            $.getJSON(baseURL + "oss/file/config", function(r){
				vm.config = r.data;
            });
        },
		
        del: function () {
            var ossIds = getSelectedRows();
            if(ossIds == null){
                return ;
            }

            confirm('确定要删除选中的记录？', function(){
                $.ajax({
                    type: "POST",
                    url: baseURL + "oss/pdf/delete",
                    contentType: "application/json",
                    data: JSON.stringify(ossIds),
                    success: function(r){
                        if(r.code === 200){
                            alert('操作成功', function(){
                                vm.reload();
                            });
                        }else{
                            alert(r.msg);
                        }
                    }
                });
            });
        },
		reload: function () {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{ 
                page:page
            }).trigger("reloadGrid");
		}
	}
});