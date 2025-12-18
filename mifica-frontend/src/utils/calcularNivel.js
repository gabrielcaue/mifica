export default function calcularNivel(reputacao) {
  if (reputacao >= 1000) return 'Mestre';
  if (reputacao >= 500) return 'Avançado';
  if (reputacao >= 100) return 'Intermediário';
  return 'Iniciante';
}
