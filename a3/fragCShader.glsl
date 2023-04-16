#version 410

in vec3 tc;
out vec4 fragColor;

uniform mat4 v_matrix;
uniform mat4 p_matrix;
uniform samplerCube s;

void main(void)
{
	fragColor = texture(s, tc);
}
