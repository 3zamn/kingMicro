
var vm = new Vue({
	  el:'#rrapp',
	    data:{
	        showList: true,
	        head:{
	        	name:null,
	        	description:null,
	        	bodyList:{
		        	title:"",
		        	tag:null,
		        	description:null,
		        	url:null,
		        	requestType:null,
		        	requestForm:null,
		        	responseForm:null,
		        	requestParam:null,
		        	responseParam:null,
		        	requestList:{name:null,type:null,paramType:null,remark:null},
		        	responseList:{name:null,description:null,remark:null}
		        }
	        },
		    info:{
				description : null,
				version : null,
				title : null,
				termsOfService : null,
				host : null,
				basePath : null,
				author : null,
				email : null,
				date : null
			}
	    },
	    created: function(){
    		this.getDoc();
    	},
    methods: {
        getDoc: function () {
        	var token = localStorage.getItem("token");
			 $.get(baseURL + "exportDoc?token="+token, function(r){
				// debugger
			        vm.head =  r.data.head;
			        vm.info =  r.data.info;
			   });
		}
       
    }
});