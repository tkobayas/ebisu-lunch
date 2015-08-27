$(document).ready(function() {
	function getAllRestaurants() {		
		$.ajax({
			 
		     url:"http://localhost:8080/ebisu-lunch-rest/rest/restaurants/",
		     dataType: 'jsonp', // Notice! JSONP <-- P (lowercase)
		     async : false,
		     success:function(restaurants){
		    	 localStorage.clear();
		    	 for (var idx in restaurants) {
		    		 var restaurant = restaurants[idx];
		    		 var str = JSON.stringify(restaurant);
		    		 //console.log(str);
		    		 localStorage.setItem(restaurant.id, str);
		    	 }
		     },
		     error:function(){
		         console.error("Error");
		     }      
		});
	}

	function listAllRestaurants() {
		$("#list tbody").empty();
		var rec = "";
		var restaurants = new Array(localStorage.length);
		for (var key in localStorage) {
			var value = localStorage.getItem(key);
			if (!value) {
				continue;
			}
			console.log(key);
			console.log(value);

			try {
				var restaurant = JSON.parse(value);
			} catch (event) {
				console.log(event);
				continue;
			}
			restaurants[restaurant.id] = restaurant;
		}
		
		console.log(restaurants);
		restaurants.sort();
		console.log(restaurants);
		
		for (var i in restaurants) {
			var restaurant = restaurants[i];
			console.log(i);
			console.log(restaurant);
			rec += "<tr id='" + key + "'>";
			rec += "<td>" + restaurant.name + "</td>";
			rec += "<td>" + restaurant.area + "</td>";
			rec += "<td>" + restaurant.tags[0].value + "</td>";
			rec += "</tr>";
		}
		console.log(rec);
		$("#list tbody").append(rec);
	}

	$('#reload').click(function() {
		getAllRestaurants();
		listAllRestaurants();
	});

	//getAllRestaurants()
	listAllRestaurants();
});
