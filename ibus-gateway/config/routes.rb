IbusGateway::Application.routes.draw do
  #map builder
  match '/lines' => "mapbuilder#initiate" ,:via => :post
  match '/lines/:id/point' =>"mapbuilder#addpoint" ,:via => :post
  match '/lines/:id/station' => "mapbuilder#addstation" ,:via => :post
  match '/lines/:id/laststation' => "mapbuilder#complete" ,:via => :post
  
  #map query
  match '/stations/by_center/:lat/:long/:latoffst/:longoffst' => "map#stations_by_center" ,:via => :get
  match '/:ses/stations/by_center/:lat/:long/:latoffst/:longoffst' => "map#stations_by_center" ,:via => :get
  match '/stations/by_center' => "map#stations_by_center" ,:via => :get

  match '/stations/by_corners/:lat1/:long1/:lat2/:long2' => "map#stations_by_corners" ,:via => :get
  match '/:ses/stations/by_corners/:lat1/:long1/:lat2/:long2' => "map#stations_by_corners" ,:via => :get
  match '/stations/by_corners' => "map#stations_by_coner" ,:via => :get
  
  match '/lines/:id/stations'=> "map#line_stations", :via => :get
  match '/:ses/lines/:id/stations'=> "map#line_stations", :via => :get
  match '/lines/:id/:level' => "map#line_details", :via => :get
  match '/:ses/lines/:id/:level' => "map#line_details", :via => :get
  match '/lines/:id' => "map#line_details", :via => :get
  match '/:ses/lines/:id' => "map#line_details", :via => :get
    
  match '/lines/:id' => "map#delete_line", :via => :delete
  match '/:ses/lines/:id' => "map#delete_line", :via => :delete
  match '/submap/:id/lines' => "map#lines_in_submap", :via => :get
  match '/:ses/submap/:id/lines' => "map#lines_in_submap", :via => :get
  
  match '/stations/:id/lines'=>"map#lines_for_station", :via => :get
  match '/stations/:id'=>"map#station_details", :via => :get
  match '/:ses/stations/:id/lines'=>"map#lines_for_station", :via => :get
  match '/:ses/stations/:id'=>"map#station_details", :via => :get
  
  match '/lines/:id/by_center/:lat/:long/:latoffst/:longoffst/:level'=>"map#line_details_in_area_by_center", :via => :get
  match '/lines/:id/by_center/'=>"map#line_details_in_area_by_center", :via => :get
  match '/lines/:id/by_corners/:lat1/:long1/:lat2/:long2/:level'=>"map#line_details_in_area_by_corners", :via => :get
  match '/lines/:id/by_corners/'=>"map#line_details_in_area_by_center", :via => :get
  match '/:ses/lines/:id/by_center/:lat/:long/:latoffst/:longoffst/:level'=>"map#line_details_in_area_by_center", :via => :get
  match '/:ses/lines/:id/by_center/'=>"map#line_details_in_area_by_center", :via => :get
  match '/:ses/lines/:id/by_corners/:lat1/:long1/:lat2/:long2/:level'=>"map#line_details_in_area_by_corners", :via => :get
  match '/:ses/lines/:id/by_corners/'=>"map#line_details_in_area_by_center", :via => :get


  match '/lines/:id/stations/by_center/:lat/:long/:latoffst/:longoffst'=>"map#line_stations_in_area_by_center", :via => :get
  match '/lines/:id/stations/by_center/'=>"map#line_stations_in_area_by_center", :via => :get
  match '/lines/:id/stations/by_corners/:lat1/:long1/:lat2/:long2'=>"map#line_stations_in_area_by_corners", :via => :get
  match '/lines/:id/stations/by_corners/'=>"map#line_stations_in_area_by_corners", :via => :get
  match '/:ses/lines/:id/stations/by_center/:lat/:long/:latoffst/:longoffst'=>"map#line_stations_in_area_by_center", :via => :get
  match '/:ses/lines/:id/stations/by_center/'=>"map#line_stations_in_area_by_center", :via => :get
  match '/:ses/lines/:id/stations/by_corners/:lat1/:long1/:lat2/:long2'=>"map#line_stations_in_area_by_corners", :via => :get
  match '/:ses/lines/:id/stations/by_corners/'=>"map#line_stations_in_area_by_corners", :via => :get

  
  match '/area/by_center/:lat/:long/:latoffst/:longoffst'=>"map#area_details_by_center", :via => :get
  match '/area/by_center/'=>"map#area_details_by_center", :via => :get
  match '/area/by_corners/:lat1/:long1/:lat2/:long2'=>"map#area_details_by_corners", :via => :get
  match '/area/by_corners/'=>"map#area_details_by_corners", :via => :get
  match '/:ses/area/by_center/:lat/:long/:latoffst/:longoffst'=>"map#area_details_by_center", :via => :get
  match '/:ses/area/by_center/'=>"map#area_details_by_center", :via => :get
  match '/:ses/area/by_corners/:lat1/:long1/:lat2/:long2'=>"map#area_details_by_corners", :via => :get
  match '/:ses/area/by_corners/'=>"map#area_details_by_corners", :via => :get
  
  #navigation
  match '/route/:lat1/:long1/:lat2/:long2/:submap'=>"navigation#navigate", :via => :get
  match '/route/'=>"navigation#navigate", :via => :get
  match '/:ses/route/:lat1/:long1/:lat2/:long2/:submap'=>"navigation#navigate", :via => :get
  match '/:ses/route/'=>"navigation#navigate", :via => :get

  #session
  match '/session'=>"session#create_session", :via => :post
  match '/session/:session'=>"session#validate_session", :via => :get
  
  #tracer
  
  match '/user/:session/routelocation/' =>"tracing#update_location_on_route", :via=>:post
  match '/user/:session/location' =>"tracing#update_location", :via=>:post
  match '/user/:session/location' =>"tracing#get_location", :via=>:get
  match '/user/:session/checkin' =>"tracing#checkin_to_route", :via=>:post
  match '/user/:session/checkin' =>"tracing#get_checkin_status", :via=>:get
  
end
