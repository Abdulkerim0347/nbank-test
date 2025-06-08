package requests.skelethon.interfaces;

import models.BaseModel;

public interface CrudEndpointInterface {
    Object post(BaseModel model);
    Object update(BaseModel model);
    Object get(BaseModel model);
    Object delete(int id);
}
