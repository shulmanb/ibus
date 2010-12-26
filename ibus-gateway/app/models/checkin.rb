class Checkin
    include ActiveModel::Serializers::JSON
    include ActiveModel::Serializers::Xml
  
    attr_accessor :route
    
  def attributes
    @attributes ||= {'route' => nil}
  end
 
end