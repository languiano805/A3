#version 410

layout (location=0) in vec3 position;
layout (location=1) in vec2 tex_coords;
layout (location=2) in vec3 vertNormal;

out vec3 varyingNormal;
out vec3 varyingLightDir;
out vec3 varyingVertPos;
out vec3 varyingHalfVector;

struct PositionalLight{
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


out vec2 tc;

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
	varyingVertPos = (m_matrix * vec4(position,1.0)).xyz;
	varyingLightDir = light.position.xyz - varyingVertPos;
	varyingNormal = (norm_matrix * vec4(vertNormal,1.0)).xyz;

	//varyingHalfVector = normalize(normalize(varyingLightDir) + normalize(-varyingVertPos)).xyz;

	 gl_Position = p_matrix * v_matrix * m_matrix * vec4(position,1.0);
	// //varyingColor = vec4(position,1.0)*0.5 + vec4(0.5, 0.5, 0.5, 0.5);
	tc = tex_coords;
} 
