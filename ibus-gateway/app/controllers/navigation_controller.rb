#provides the navigation API
class NavigationController < ApplicationController
  
  def navigate
      long1 = params[:long1].gsub("_",".")
      lat1 = params[:lat1].gsub("_",".")
      long2 = params[:long2].gsub("_",".")
      lat2 = params[:lat2].gsub("_",".")
      submap = params[:submap]
      @route = @@wrapper.navigate(lat1,long1,lat2,long2,submap)
      format @route  
    end  
end
