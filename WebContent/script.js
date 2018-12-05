/**
 * 
 */
var selected={creatorName:"", createTime:"", name:""};
var selectedUser="";
var forMe = false;
var clicked = false;
$(document).ready(function()
	{
	console.log("HEEEEEY");
	//alert("HEEEY");
	$.get( "./userSection", function( data ) {
		 var userData = JSON.parse(data);
		 console.log(userData);
		 var table = $('<table>');
		 $.each(userData.otherUsers, function(index,value)
				 {
			 		var field = $('<td>').text(value);
			 		var row = $('<tr>');
			 		row.append(field);
			 		row.click(function(){
			 			selectedUser = field.text();
			 			console.log("Was clicked");
	 					console.log(selectedUser);
			 		});
			 		table.append(row);
				 });
		 $("#userSection").empty();
		 $("#userSection").append($("<h1>").text("List of other users of system"));
		 $("#userSection").append(table);
		 var table = $('<table>');
		 var header = $('<tr>');
		 header.append($('<th>').text("Name of recipient of the file"));
		 header.append($('<th>').text("Date and time of encrypting of the file"));
		 header.append($('<th>').text("File name"));
		 table.append(header);
		 $.each(userData.filesFromUser, function(index,value)
				 {
			 		var row = $('<tr>');
			 		var field =$('<td>').text( value.recipientName);
			 		row.append(field);
			 		field= $('<td>').text(value.createDate);
			 		//console.log(value.createDate);
			 		row.append(field);
			 		field = $('<td>').text(value.name);
			 		row.append(field);
			 		row.click(function()
			 		{
	 					//selected.creatorName = $(this).find('td:first').text();
			 			selected.creatorName=$("#myName").text();
			 			console.log(selected.creatorName);
	 					selected.createTime = $(this).find('td:nth-child(2)').text();
	 					selected.name = $(this).find('td:nth-child(3)').text();
	 					console.log(selected);
	 					forMe = false;
	 					clicked = true;
			 					
			 		});
			 		table.append(row);
				 });
		 $("#filesFromMe").empty();
		 $("#filesFromMe").append($('<h1>').text("Files you have encrypted for other users"));
		 $("#filesFromMe").append(table);
		 
		 var table = $('<table>');
		 var header = $('<tr>');
		 header.append($('<th>').text("Name of creator of the file"));
		 header.append($('<th>').text("Date and time of encrypting of the file"));
		 header.append($('<th>').text("File name"));
		 table.append(header);
		 $.each(userData.filesForUser, function(index,data)
				 {
			 		var row = $('<tr>');
			 		var field = $('<td>').text(data.creatorName);
			 		row.append(field);
			 		field = $('<td>').text(data.createTime);
			 		row.append(field);
			 		field = $('<td>').text(data.name);
			 		row.append(field);
			 		row.click(function()
			 				{
			 					selected.creatorName = $(this).find('td:first').text();
			 					selected.createTime = $(this).find('td:nth-child(2)').text();
			 					selected.name = $(this).find('td:nth-child(3)').text();
			 					forMe = true;
			 					clicked = true;
			 					console.log(selected);
			 				});
			 		table.append(row);
				 });
		 $("#filesForMe").empty();
		 $("#filesForMe").append($('<h1>').text("Files other users encrypted for you"));
		 $("#filesForMe").append(table);
		});
		$("#getCommentsButton").click(function(){
			$.post("./userSection", {mode: "comments", createTime: selected.createTime, creatorName: selected.creatorName}).done(function (data){
				$("#commentSection").empty();
				var commentsData = JSON.parse(data);
				$.each(commentsData, function(index,value){
					var article = $("<article>");
					article.append($("<h1>").text(value.creator));
					article.append($("<h2>").text(value.message));
					article.append($("<p>").text(value.message));
				});
			});
		});
		
		$("#encryptForm").submit(function(eventObj){
			eventObj.preventDefault();
			 $(":hidden").remove();
			if(selectedUser === "")
			{
				alert("You must select a user to encrypt for")
				return;
			}
			$('<input />').attr('type', 'hidden')
	          .attr('name', "mode")
	          .attr('value', "encrypt")
	          .appendTo('#encryptForm');
			$('<input />').attr('type', 'hidden')
	          .attr('name', "recipient")
	          .attr('value', selectedUser)
	          .appendTo('#encryptForm');
			var formData = new FormData($('#encryptForm')[0]);
			$.ajax({
				type: "POST",
				url: "./userSection",
				cache       : false,
		        contentType : false,
		        processData : false,
		        data :		 formData,
		        success     : function(data, textStatus, jqXHR){
		            // Callback code
		        }
			});
			
		});
		$("#decryptForm").submit(function(event){
			 $(":hidden").remove();
			if((clicked == false) || (forMe == false))
			{
				event.preventDefault();
				alert("You must select a file that was encrypted for you");
				return false;
			}
			$('<input />').attr('type', 'hidden')
	          .attr('name', "mode")
	          .attr('value', "decrypt")
	          .appendTo('#decryptForm');
			$('<input />').attr('type', 'hidden')
	          .attr('name', "createTime")
	          .attr('value', selected.createTime)
	          .appendTo('#decryptForm');
			$('<input />').attr('type', 'hidden')
	          .attr('name', "creatorName")
	          .attr('value', selected.creatorName)
	          .appendTo('#decryptForm');
			return true;
		});
	})