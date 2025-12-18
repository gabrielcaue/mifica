export default function CardConquista({ titulo, descricao }) {
  return (
    <div className="bg-purple-100 p-4 rounded shadow">
      <h3 className="font-bold text-lg">{titulo}</h3>
      <p className="text-sm text-gray-700">{descricao}</p>
    </div>
  );
}
