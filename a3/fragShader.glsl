#version 410

in vec3 varyingNormal;
in vec3 varyingLightDir;
in vec3 varyingVertPos;
in vec3 varyingHalfVector;

out vec4 fragColor;

vec4 textureColor;
vec4 lightingColor;

struct PositionalLight
{
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    vec3 position;
};

struct Material
{
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    float shininess;
};

//in vec4 varyingColor;
in vec2 tc;

uniform vec4 globalAmbient;
uniform PositionalLight light;
uniform Material material;
uniform mat4 m_matrix;
uniform mat4 v_matrix;
uniform mat4 p_matrix;
uniform mat4 norm_matrix;
uniform sampler2D s;

void main(void)
{	
    //normalize the light, normal, and view vectors
    vec3 L = normalize(varyingLightDir);
    vec3 N = normalize(varyingNormal);
    vec3 V = normalize(-v_matrix[3].xyz - varyingVertPos);

    // //get the angle between the light and surface normal;
    float cosTheta = dot(L, N);

    // //halfway bector varyingHalfVector was computed in the vertex shader, 
    // //and interpolated prior to reaching the fragment shader.
    // //It is copied into variable H here for convenience later.
    //vec3 H = normalize(varyingHalfVector);
    vec3 R = normalize(reflect(-L, N));

    // //get angle betwen the normal and the halfway vector
    //float cosPhi = dot(H, N);
    float cosPhi = dot(V, R);

    // //compute ADS contributions (per pixel):
    vec3 ambient = ((globalAmbient * material.ambient) + (light.ambient * material.ambient)).xyz;
    vec3 diffuse = (light.diffuse.xyz * material.diffuse.xyz) * max(cosTheta, 0.0);
    vec3 specular = (light.specular.xyz * material.specular.xyz) * pow(max(cosPhi, 0.0), material.shininess);

    textureColor = texture(s, tc);

    // //compute the final color
    lightingColor = vec4(ambient + diffuse + specular, 1.0) * textureColor;
    fragColor = lightingColor;

    //fragColor = texture(s, tc);

   
}
