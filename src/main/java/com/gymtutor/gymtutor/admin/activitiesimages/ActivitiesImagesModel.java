package com.gymtutor.gymtutor.admin.activitiesimages;


import com.gymtutor.gymtutor.admin.activities.ActivitiesModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(
        name ="tb_activitiesimages"
)
public class ActivitiesImagesModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int imageId;

    @NotBlank(message = "Nome da imagem não pode estar vazio!")
    @Size(min = 3, max = 50, message = "Nome da imagem deve ter entre 3 e 50 caracteres.")
    private String imageName;

    @ManyToOne
    @JoinColumn(name = "activity_id",  referencedColumnName = "activitiesId")
    private ActivitiesModel activity;

//    @NotBlank(message = "Caminho da imagem não pode estar vazio!") precisa ser em branco, pois o caminho não será validado no formulário, NÃO ATIVE ESSA LINHA!!!!!
    @Column(name = "image_path")
    private String imagePath;


    public ActivitiesImagesModel() {
    }


    public int getImageId() {
        return imageId;
    }


    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public ActivitiesModel getActivity() {
        return activity;
    }

    public void setActivity(ActivitiesModel activity) {
        this.activity = activity;
    }

}
