$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/scheduleLog/list',
        datatype: "json",
        colModel: [			
            { label: '日志ID', name: 'logId', width: 50, key: true },
			{ label: '任务ID', name: 'jobId', width: 50},
			{ label: 'bean名称', name: 'beanName', width: 60 },
			{ label: '方法名称', name: 'methodName', width: 60 },
			{ label: '参数', name: 'params', width: 60 },
			{ label: '状态', name: 'status', width: 50, formatter: function(value, options, row){
				return value === true ? 
					'<span class="label label-success">成功</span>' :
					'<span class="label label-danger pointer" onclick="vm.showError('+row.logId+')">失败</span>';
			}},
			{ label: '耗时(单位：毫秒)', name: 'times', width: 70 },
			{ label: '执行时间', name: 'createTime', width: 80 }
        ],
		viewrecords: true,
        height: 385,
        rowNum: 10,
		rowList : [10,30,50,100,200],
        rownumbers: true, 
        rownumWidth: 25, 
        autowidth:true,
        multiselect: false,
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
        action: baseURL + 'sys/scheduleLog/upload?token=' + token,
        name: 'file',
        autoSubmit:true,
        responseType:"json",
        onSubmit:function(file, extension){
         /*   if(vm.config.type == null){
                alert("云存储配置未配置");
                return false;
            }*/
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
		q:{
			jobId: ''
		}
	},
	methods: {
		query: function () {
			$("#jqGrid").jqGrid('setGridParam',{ 
                postData:{'jobId': vm.q.jobId},
                page:1 
            }).trigger("reloadGrid");
		},
		exportExcel: function () {
			var token = localStorage.getItem("token");
			 var url= baseURL + "sys/scheduleLog/export?jobId=" + vm.q.jobId+"&token="+token+"&limit="+2000000+"&page="+1;
			location.href=encodeURI(url)//转码下以免被高版本tomcat过滤特殊字符报错
		},
		showError: function(logId) {
			$.get(baseURL + "sys/scheduleLog/info/"+logId, function(r){
				parent.layer.open({
				  title:'失败信息',
				  closeBtn:0,
				  content: r.data.error
				});
			});
		},
		back: function () {
			history.go(-1);
		}
	}
});

